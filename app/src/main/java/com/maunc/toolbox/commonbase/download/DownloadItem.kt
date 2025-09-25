package com.maunc.toolbox.commonbase.download

import java.io.File

sealed class DownloadItem(
    open val dirToDownload: String,
    open val url: String,
    open val size: Long,
) {
    fun getAbsoluteDownloadPath(): String {
        return dirToDownload + File.separator + url.substringAfterLast("/")
    }
}