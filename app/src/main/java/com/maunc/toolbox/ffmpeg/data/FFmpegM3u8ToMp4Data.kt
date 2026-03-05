package com.maunc.toolbox.ffmpeg.data

import android.graphics.Bitmap

/**
 * m3u8对象
 */
data class FFmpegM3u8ResultData(
    val m3u8FileName: String,
    val m3u8FilePath: String,
    val totalSegmentCount: Int,
    val segmentList: List<FFmpegM3u8TsSegmentData>,
    val coverBitmap: Bitmap? = null,
    val isStandardM3u8: Boolean,
    val localSegmentCount: Int,
    val isAvailable: Boolean,
)

/**
 * m3u8片段对象
 */
data class FFmpegM3u8TsSegmentData(
    val index: Int,
    val duration: Float,
    val filePath: String,
    val fileSize: Long,
    val isFileExist: Boolean,
)