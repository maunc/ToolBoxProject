package com.maunc.toolbox.signaturecanvas.data

data class CanvasSaveFileData(
    val id: Long,
    val uri: String,
    val width:Int,
    val height:Int,
    val fileName: String,
    val fileSize: Long,
    val addDateTime: Long,
)