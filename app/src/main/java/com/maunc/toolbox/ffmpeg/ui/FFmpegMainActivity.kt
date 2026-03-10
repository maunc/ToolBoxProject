package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivityFfmpegMainBinding
import com.maunc.toolbox.ffmpeg.adapter.FFmpegConfigAdapter
import com.maunc.toolbox.ffmpeg.data.FFmpegConfigData
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegMainViewModel

class FFmpegMainActivity : BaseActivity<FFmpegMainViewModel, ActivityFfmpegMainBinding>() {

    private val configAdapter by lazy {
        FFmpegConfigAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                when (data[pos].type) {
                    FFmpegConfigData.MP4_TO_MP3_TYPE -> startTargetActivity(FFmpegMp4ToMp3Activity::class.java)

                    FFmpegConfigData.M3U8_TO_MP4_TYPE -> startTargetActivity(FFmpegM3u8ToMp4Activity::class.java)

                    FFmpegConfigData.H265_TO_MP4_TYPE -> startTargetActivity(FFmpegM3u8ToMp4Activity::class.java)
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.tool_box_item_ffmpeg_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.ffmpegMainTypeRecycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegMainTypeRecycler.adapter = configAdapter
        configAdapter.setList(mViewModel.initRecyclerData())
        mViewModel.createFFmpegDir()
    }

    override fun createObserver() {}
}