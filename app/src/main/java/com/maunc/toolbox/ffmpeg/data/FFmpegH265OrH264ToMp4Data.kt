package com.maunc.toolbox.ffmpeg.data

data class FFmpegH265OrH264ToMp4ResultData(
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val time: Long,
    val timeString: String,
)