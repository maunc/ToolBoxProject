package com.maunc.torrent

import android.util.Log
import org.libtorrent4j.SessionManager
import org.libtorrent4j.Sha1Hash
import org.libtorrent4j.TorrentInfo
import org.libtorrent4j.swig.byte_vector
import org.libtorrent4j.swig.sha1_hash
import org.libtorrent4j.swig.torrent_flags_t
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

object TorrentManager {

    private const val TAG = "TorrentManager"

    private val btihRegex = Regex("(?i)btih:([^&]+)")

    private const val BASE32 = "234567abcdefghijklmnopqrstuvwxyz"

    private val session = SessionManager()
    private val started = AtomicBoolean(false)

    fun init() {
        if (!started.compareAndSet(false, true)) {
            return
        }
        try {
            session.start()
        } catch (t: Throwable) {
            started.set(false)
            Log.e(TAG, "LibTorrentSession.start", t)
            throw t
        }
    }

    /**
     * 应用退出时可调用（系统通常不会走 [android.app.Application.onTerminate]）。
     */
    fun unInit() {
        if (!started.compareAndSet(true, false)) {
            return
        }
        try {
            session.stop()
        } catch (t: Throwable) {
            Log.e(TAG, "LibTorrentSession.stop", t)
        }
    }

    /**
     * 从本地 .torrent 文件开始下载。
     */
    fun downloadTorrentFile(torrentPath: String, saveDir: File) {
        init()
        val file = File(torrentPath)
        require(file.exists() && file.isFile) { "种子文件无效: $torrentPath" }
        val ti = TorrentInfo(file)
        session.download(ti, saveDir)
    }

    /**
     * 从磁链开始下载（阻塞拉取 metadata；无进度回调）。
     */
    fun downloadMagnet(magnetUri: String, saveDir: File, magnetFetchTimeoutSec: Int = 180) {
        init()
        val uri = magnetUri.trim()
        require(uri.startsWith("magnet:", ignoreCase = true)) { "不是磁链链接" }
        val torrentBytes = session.fetchMagnet(uri, magnetFetchTimeoutSec, saveDir)
            ?: error("拉取磁链元数据超时或失败，请检查网络与 DHT")
        val ti = TorrentInfo(torrentBytes)
        session.download(ti, saveDir)
    }

    /**
     * 磁链下载：使用 [SessionManager.download] 非阻塞加入会话，再通过 [SessionManager.find] 轮询进度。
     * @param cancel 返回 true 时结束轮询（如协程取消）
     * @param addTimeoutMs 会话仍未出现有效时的超时
     */
    fun downloadMagnetWithProgress(
        magnetUri: String,
        saveDir: File,
        pollIntervalMs: Long = 450L,
        addTimeoutMs: Long = 180_000L,
        cancel: () -> Boolean = { false },
        progressCallback: (TorrentDownloadProgress) -> Unit = {},
    ) {
        init()
        val uri = magnetUri.trim()
        require(uri.startsWith("magnet:", ignoreCase = true)) { "不是磁链链接" }
        val infoHash = sha1HashFromMagnet(uri)
        session.download(uri, saveDir, torrent_flags_t())

        val t0 = System.currentTimeMillis()
        while (!cancel()) {
            Thread.sleep(pollIntervalMs)
            val th = session.find(infoHash)
            if (!th.isValid) {
                progressCallback.invoke(
                    TorrentDownloadProgress(
                        phase = 0,
                        progress01 = 0f,
                        torrentName = null,
                        stateDescription = "正在连接 DHT / 拉取 metadata…",
                        downloadRateBytesPerSec = 0L,
                        totalDoneBytes = 0L,
                        totalBytes = 0L,
                    ),
                )
                if (System.currentTimeMillis() - t0 > addTimeoutMs) {
                    error("等待种子加入会话超时，请检查网络与磁链是否有效")
                }
                continue
            }

            val st = runCatching { th.status() }.getOrNull()
            if (st == null) {
                continue
            }

            val name = runCatching { st.name() }.getOrNull()
            val prog = runCatching { st.progress() }.getOrDefault(0f)
            val rate = runCatching { st.downloadRate().toLong() }.getOrDefault(0L)
            val done = runCatching { st.totalDone() }.getOrDefault(0L)
            val total = runCatching { st.totalWanted() }.getOrDefault(0L)
            val stateStr = runCatching { st.state().toString() }.getOrElse { "下载中" }

            progressCallback.invoke(
                TorrentDownloadProgress(
                    phase = 1,
                    progress01 = prog,
                    torrentName = name,
                    stateDescription = stateStr,
                    downloadRateBytesPerSec = rate,
                    totalDoneBytes = done,
                    totalBytes = total,
                ),
            )

            if (prog >= 0.999f) {
                return
            }
        }
    }

    private fun sha1HashFromMagnet(magnetUri: String): Sha1Hash {
        val raw = btihRegex.find(magnetUri)?.groupValues?.get(1)?.trim()
            ?: throw IllegalArgumentException("磁链中未找到 btih")
        val token = raw.lowercase()
        val twentyBytes: ByteArray = when {
            token.length == 40 && token.all { it.isHexDigitCompat() } ->
                hex40ToBytes(token)

            token.length == 32 && token.all { it in BASE32 } ->
                sha1BytesFromBtihBase32(token)

            else -> throw IllegalArgumentException("无法识别的 btih 格式")
        }
        return Sha1Hash(sha1_hash(bytesToVector(twentyBytes)))
    }

    /**
     * 32 字符 BitTorrent base32 → 20 字节 SHA1。
     */
    private fun sha1BytesFromBtihBase32(chars: String): ByteArray {
        val out = ByteArray(20)
        var bitBuffer = 0L
        var bitCount = 0
        var outIndex = 0
        for (c in chars.lowercase()) {
            val v = BASE32.indexOf(c)
            require(v >= 0)
            bitBuffer = bitBuffer shl 5 or v.toLong()
            bitCount += 5
            if (bitCount >= 8) {
                bitCount -= 8
                val byte = (bitBuffer shr bitCount) and 0xFFL
                out[outIndex++] = byte.toInt().toByte()
            }
        }
        require(outIndex == 20)
        return out
    }

    private fun bytesToVector(bytes: ByteArray): byte_vector {
        val v = byte_vector()
        for (b in bytes) v.add(b)
        return v
    }

    private fun Char.isHexDigitCompat() = this in '0'..'9' || this in 'a'..'f'

    private fun hex40ToBytes(hex: String): ByteArray {
        val out = ByteArray(20)
        for (i in 0 until 20) {
            out[i] = hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return out
    }
}