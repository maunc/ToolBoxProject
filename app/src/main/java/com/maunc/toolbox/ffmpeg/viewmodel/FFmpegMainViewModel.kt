package com.maunc.toolbox.ffmpeg.viewmodel

import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.R
import com.maunc.base.utils.createFileDir
import com.maunc.base.utils.createFileDirFromSdCard
import com.maunc.toolbox.ffmpeg.constant.H265_OR_H264_TO_MP4_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.M3U8_TO_MP4_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.MERGE_MP4_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_GIF_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_MP3_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.SAVE_ROOT_PATH
import com.maunc.toolbox.ffmpeg.constant.SAVE_ROOT_PATH_NAME
import com.maunc.toolbox.ffmpeg.data.FFmpegConfigData

class FFmpegMainViewModel : BaseViewModel<BaseModel>() {

    fun initRecyclerData() = mutableListOf<FFmpegConfigData>().mutableListInsert(
        FFmpegConfigData(
            type = FFmpegConfigData.MP4_TO_MP3_TYPE,
            title = obtainString(R.string.ffmpeg_main_item_one)
        ),
        FFmpegConfigData(
            type = FFmpegConfigData.M3U8_TO_MP4_TYPE,
            title = obtainString(R.string.ffmpeg_main_item_two)
        ),
        FFmpegConfigData(
            type = FFmpegConfigData.H265_OR_H264_TO_MP4_TYPE,
            title = obtainString(R.string.ffmpeg_main_item_three)
        ),
        FFmpegConfigData(
            type = FFmpegConfigData.MERGE_MP4_TYPE,
            title = obtainString(R.string.ffmpeg_main_item_four)
        ),
        FFmpegConfigData(
            type = FFmpegConfigData.MP4_TO_GIF_TYPE,
            title = obtainString(R.string.ffmpeg_main_item_five)
        ),
    )

    fun createFFmpegDir() {
        createFileDirFromSdCard(SAVE_ROOT_PATH_NAME)
        createFileDir(SAVE_ROOT_PATH, MP4_TO_MP3_SAVE_PATH_NAME)
        createFileDir(SAVE_ROOT_PATH, MP4_TO_GIF_SAVE_PATH_NAME)
        createFileDir(SAVE_ROOT_PATH, M3U8_TO_MP4_SAVE_PATH_NAME)
        createFileDir(SAVE_ROOT_PATH, H265_OR_H264_TO_MP4_SAVE_PATH_NAME)
        createFileDir(SAVE_ROOT_PATH, MERGE_MP4_SAVE_PATH_NAME)
    }
}