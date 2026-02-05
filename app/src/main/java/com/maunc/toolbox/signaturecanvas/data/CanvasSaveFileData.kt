package com.maunc.toolbox.signaturecanvas.data

import android.net.Uri

data class CanvasSaveFileData(
    val uri: Uri,
    val fileName: String,
    val fileSize: Long,
    val addDateTime: Long,
)