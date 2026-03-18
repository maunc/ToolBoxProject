package com.maunc.ftp

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

val ftpConnectManager: FtpConnectManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    FtpConnectManager.ftpConnectManager
}

/**
 * FTP 客户端管理器：用于连接“其他 FTP 服务器”
 */
class FtpConnectManager private constructor() {

    companion object {
        const val TAG = "FtpConnectManager"

        // 1MB 缓冲区：通常能显著提高吞吐（视网络与设备而定）
        private const val IO_BUFFER_SIZE = 1024 * 1024

        // 控制连接保活（秒），避免长时间传输被服务器踢下线
        private const val CONTROL_KEEP_ALIVE_SECONDS = 30L

        val ftpConnectManager: FtpConnectManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FtpConnectManager()
        }
    }

    private var client: FTPClient? = null

    fun isConnected(): Boolean = client?.isConnected == true

    fun disconnect() {
        val c = client
        client = null
        try {
            if (c?.isConnected == true) {
                try {
                    c.logout()
                } catch (_: Exception) {
                }
                try {
                    c.disconnect()
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
    }

    fun connect(config: ConnectConfig): Result<String> {
        if (!config.port.isValidPort()) return Result.Failure("端口号不合理: ${config.port}")
        disconnect()
        val ftpClient = FTPClient()
        ftpClient.controlEncoding = config.controlEncoding
        ftpClient.connectTimeout = config.connectTimeoutMs
        ftpClient.defaultTimeout = config.connectTimeoutMs
        ftpClient.setDataTimeout(config.dataTimeoutMs)
        return try {
            ftpClient.connect(config.host, config.port)
            // 只有在 connect() 成功后，socket 才是非空，才能设置 soTimeout
            ftpClient.soTimeout = config.soTimeoutMs
            if (!FTPReplyHelper.isPositiveCompletion(ftpClient.replyCode)) {
                val msg =
                    "连接失败: ${ftpClient.replyString?.trim() ?: "replyCode=${ftpClient.replyCode}"}"
                try {
                    ftpClient.disconnect()
                } catch (_: Exception) {
                }
                Result.Failure(msg)
            } else if (!ftpClient.login(config.username, config.password)) {
                val msg =
                    "登录失败: ${ftpClient.replyString?.trim() ?: "replyCode=${ftpClient.replyCode}"}"
                try {
                    ftpClient.disconnect()
                } catch (_: Exception) {
                }
                Result.Failure(msg)
            } else {
                // 文件名编码：部分服务器支持该命令来切换为 UTF-8（不支持时会返回 5xx，忽略即可）
                if (config.tryEnableUtf8 && config.controlEncoding.equals(
                        "UTF-8",
                        ignoreCase = true
                    )
                ) {
                    runCatching { ftpClient.sendCommand("OPTS", "UTF8 ON") }
                    runCatching { ftpClient.sendCommand("OPTS", "UTF-8 ON") }
                }
                // 传输相关：尽量使用更适合大文件的设置
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE)
                ftpClient.bufferSize = IO_BUFFER_SIZE
                runCatching { ftpClient.controlKeepAliveTimeout = CONTROL_KEEP_ALIVE_SECONDS }
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                if (config.passiveMode) ftpClient.enterLocalPassiveMode() else ftpClient.enterLocalActiveMode()
                client = ftpClient
                Result.Success("登录成功")
            }
        } catch (t: Throwable) {
            try {
                ftpClient.disconnect()
            } catch (_: Exception) {
            }
            Result.Failure("连接异常: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun list(path: String = "."): Result<List<FTPFile>> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            Result.Success(c.listFiles(path)?.toList() ?: emptyList())
        } catch (t: Throwable) {
            Result.Failure("列目录失败: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun mkdir(path: String): Result<Boolean> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            Result.Success(c.makeDirectory(path))
        } catch (t: Throwable) {
            Result.Failure("创建目录失败: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun delete(remotePath: String): Result<Boolean> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            Result.Success(c.deleteFile(remotePath))
        } catch (t: Throwable) {
            Result.Failure("删除失败: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    /**
     * 下载文件（带进度回调）
     * @param totalBytes 远端文件大小（可选）。如果你从 list() 拿到了 size，可以传进来用于计算百分比。
     * @param onProgress 进度回调：downloadedBytes, totalBytes
     */
    fun download(
        remotePath: String,
        localFile: File,
        totalBytes: Long = 0L,
        onProgress: (downloadedBytes: Long, totalBytes: Long?) -> Unit = { _, _ -> },
    ): Result<Unit> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            localFile.parentFile?.mkdirs()
            c.bufferSize = IO_BUFFER_SIZE
            val input = c.retrieveFileStream(remotePath)
                ?: return Result.Failure("下载失败: ${c.replyString?.trim() ?: "replyCode=${c.replyCode}"}")

            BufferedInputStream(input, IO_BUFFER_SIZE).use { bin ->
                BufferedOutputStream(FileOutputStream(localFile), IO_BUFFER_SIZE).use { bout ->
                    val buf = ByteArray(IO_BUFFER_SIZE)
                    var downloaded = 0L
                    var lastEmit = 0L
                    while (true) {
                        val read = bin.read(buf)
                        if (read <= 0) break
                        bout.write(buf, 0, read)
                        downloaded += read.toLong()
                        // 最多 10 次/秒，避免 UI 卡顿
                        val now = System.currentTimeMillis()
                        if (now - lastEmit >= 100) {
                            lastEmit = now
                            onProgress(downloaded, totalBytes)
                        }
                    }
                    bout.flush()
                    onProgress(downloaded, totalBytes)
                }
            }

            // 流式传输必须调用 completePendingCommand() 才算真正完成
            if (!c.completePendingCommand()) {
                return Result.Failure("下载失败: ${c.replyString?.trim() ?: "replyCode=${c.replyCode}"}")
            }
            Result.Success(Unit)
        } catch (t: Throwable) {
            Result.Failure("下载异常: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun upload(localFile: File, remotePath: String): Result<Unit> {
        val c = client ?: return Result.Failure("未连接")
        if (!localFile.exists()) return Result.Failure("本地文件不存在: ${localFile.absolutePath}")
        return try {
            c.bufferSize = IO_BUFFER_SIZE
            BufferedInputStream(FileInputStream(localFile), IO_BUFFER_SIZE).use { input ->
                if (!c.storeFile(remotePath, input)) {
                    Result.Failure("上传失败: ${c.replyString?.trim() ?: "replyCode=${c.replyCode}"}")
                } else {
                    Result.Success(Unit)
                }
            }
        } catch (t: Throwable) {
            Result.Failure("上传异常: ${t.message ?: t::class.java.simpleName}", t)
        }
    }
}

data class ConnectConfig(
    var host: String,
    var port: Int,
    var username: String,
    var password: String,
    var connectTimeoutMs: Int = 15_000,
    var dataTimeoutMs: Int = 30_000,
    var soTimeoutMs: Int = 30_000,
    var passiveMode: Boolean = true,
    var controlEncoding: String = "UTF-8",
    /**
     * 登录成功后尝试发送 OPTS UTF8 ON（部分服务器通过此命令启用 UTF-8 文件名）
     */
    var tryEnableUtf8: Boolean = true,
)

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure<T>(val reason: String, val throwable: Throwable? = null) : Result<T>()
}

/**
 * 避免直接依赖 org.apache.commons.net.ftp.FTPReply（保持 API 更稳定）。
 */
private object FTPReplyHelper {
    fun isPositiveCompletion(reply: Int): Boolean = reply in 200..299
}