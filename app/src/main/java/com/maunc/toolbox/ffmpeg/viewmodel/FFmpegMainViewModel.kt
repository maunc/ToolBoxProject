package com.maunc.toolbox.ffmpeg.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
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
        )
        return ffmpegMainItemData.value!!
    }
}