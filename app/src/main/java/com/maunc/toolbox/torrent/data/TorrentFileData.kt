package com.maunc.toolbox.torrent.data

/**
 * 媒体库扫描到的本地 .torrent 文件
 */
data class TorrentFileData(
    val filePath: String,
    val fileName: String,
    val fileSizeBytes: Long,
)
