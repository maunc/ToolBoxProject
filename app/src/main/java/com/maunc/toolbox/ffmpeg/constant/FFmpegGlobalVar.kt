package com.maunc.toolbox.ffmpeg.constant

import com.maunc.toolbox.commonbase.utils.obtainSDCardRootPath
import java.io.File
import java.util.Formatter
import java.util.Locale

const val FFMPEG_CREATE_DIR_TAG = "FFMPEG_CREATE_DIR"

const val SELECT_MP4_TO_MP3_MAX_NUM = 1

//最多合并视频数量
const val SELECT_MERGE_MP4_MAX_NUM = 10

const val SAVE_FFMPEG_PREFIX = "HToolBox_"

var SAVE_ROOT_PATH_NAME = "HToolBox"
var SAVE_ROOT_PATH = obtainSDCardRootPath() + File.separator + SAVE_ROOT_PATH_NAME

const val MP4_TO_MP3_SAVE_PATH_NAME = "mp4_to_mp3"
var MP4_TO_MP3_SAVE_PATH = SAVE_ROOT_PATH + File.separator + MP4_TO_MP3_SAVE_PATH_NAME

const val M3U8_TO_MP4_SAVE_PATH_NAME = "m3u8_to_mp4"
var M3U8_TO_MP4_SAVE_PATH = SAVE_ROOT_PATH + File.separator + M3U8_TO_MP4_SAVE_PATH_NAME

const val H265_OR_H264_TO_MP4_SAVE_PATH_NAME = "h265_or_h264_to_mp4"
var H265_OR_H264_TO_MP4_SAVE_PATH = SAVE_ROOT_PATH + File.separator + H265_OR_H264_TO_MP4_SAVE_PATH_NAME

const val MERGE_MP4_SAVE_PATH_NAME = "merge_mp4"
var MERGE_MP4_SAVE_PATH = SAVE_ROOT_PATH + File.separator + MERGE_MP4_SAVE_PATH_NAME

/**
 * FFmpeg 进行状态
 */
const val FFMPEG_START = 1
const val FFMPEG_SUCCESS = 2
const val FFMPEG_ERROR = 3
const val FFMPEG_NONE = 4

/**
 * 视频时长计算
 */
fun stringForTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000L
    val seconds = totalSeconds % 60L
    val minutes = totalSeconds / 60L % 60L
    val hours = totalSeconds / 3600L
    val stringBuilder = StringBuilder()
    val mFormatter = Formatter(stringBuilder, Locale.getDefault())
    return if (hours > 0L) mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
        .toString() else mFormatter.format("%02d:%02d", minutes, seconds).toString()
}