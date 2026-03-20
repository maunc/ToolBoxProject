package com.maunc.torrent

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * 下载保存目录：主外部存储根目录下的 `ToolBoxTorrents`
 * 一般为 `/storage/emulated/0/ToolBoxTorrents`，需在清单中声明存储权限；
 */
fun defaultTorrentSaveDir(context: Context): File {
    val defaultDownDir = File(
        Environment.getExternalStorageDirectory()
            ?: (context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir),
        "ToolBoxTorrents"
    )
    if (!defaultDownDir.exists()) {
        defaultDownDir.mkdirs()
    }
    return defaultDownDir
}