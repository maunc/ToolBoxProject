package com.maunc.toolbox.ffmpeg.data

data class FFmpegConfigData(
    var type: Int,
    var title: String,
) {
    companion object {
        const val MP4_TO_MP3_TYPE = 0//mp4提取音频
        const val M3U8_TO_MP4_TYPE = 1//m3u8转mp4
        const val H265_TO_MP4_TYPE = 2//H2565转mp4
    }
}
