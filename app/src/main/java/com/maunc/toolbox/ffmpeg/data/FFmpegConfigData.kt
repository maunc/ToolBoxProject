package com.maunc.toolbox.ffmpeg.data

data class FFmpegConfigData(
    var type: Int,
    var title: String,
) {
    companion object {
        const val MP4_TO_MP3_TYPE = 0//mp4提取音频
        const val M3U8_TO_MP4_TYPE = 1//m3u8转mp4
        const val H265_OR_H264_TO_MP4_TYPE = 2//H265和H264转mp4
        const val MERGE_MP4_TYPE = 3//合并多个mp4
    }
}
