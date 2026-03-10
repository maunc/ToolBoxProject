package com.maunc.toolbox.ffmpeg.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.createFileDir
import com.maunc.toolbox.commonbase.utils.createFileDirFromSdCard
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_CREATE_DIR_TAG
import com.maunc.toolbox.ffmpeg.constant.H265_OR_H264_TO_MP4_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.M3U8_TO_MP4_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_MP3_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.SAVE_ROOT_PATH
import com.maunc.toolbox.ffmpeg.constant.SAVE_ROOT_PATH_NAME
import com.maunc.toolbox.ffmpeg.data.FFmpegConfigData

class FFmpegMainViewModel : BaseViewModel<BaseModel>() {
    private var ffmpegMainItemData =
        MutableLiveData<MutableList<FFmpegConfigData>>(mutableListOf())

    fun initRecyclerData(): MutableList<FFmpegConfigData> {
        ffmpegMainItemData.value?.mutableListInsert(
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
        )
        return ffmpegMainItemData.value!!
    }

    fun createFFmpegDir() {
        Log.e(FFMPEG_CREATE_DIR_TAG, "${createFileDirFromSdCard(SAVE_ROOT_PATH_NAME)}")
        Log.e(FFMPEG_CREATE_DIR_TAG, "${createFileDir(SAVE_ROOT_PATH, MP4_TO_MP3_SAVE_PATH_NAME)}")
        Log.e(FFMPEG_CREATE_DIR_TAG, "${createFileDir(SAVE_ROOT_PATH, M3U8_TO_MP4_SAVE_PATH_NAME)}")
        Log.e(FFMPEG_CREATE_DIR_TAG, "${createFileDir(SAVE_ROOT_PATH, H265_OR_H264_TO_MP4_SAVE_PATH_NAME)}")
    }
}