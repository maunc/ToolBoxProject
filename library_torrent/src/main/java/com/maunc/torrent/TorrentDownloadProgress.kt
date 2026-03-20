package com.maunc.torrent

/**
 * 磁链任务实时状态（由 [com.maunc.toolbox.torrent.engine.LibTorrentSession] 轮询回调）。
 *
 * @param phase 0 等待会话注册种子 / 拉 metadata；1 已取得元数据，正在下载内容
 * @param progress01 libtorrent 总体进度 0..1（metadata 阶段可能长期为 0）
 */
data class TorrentDownloadProgress(
    val phase: Int,
    val progress01: Float,
    val torrentName: String?,
    val stateDescription: String,
    val downloadRateBytesPerSec: Long,
    val totalDoneBytes: Long,
    val totalBytes: Long,
)
