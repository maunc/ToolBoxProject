package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import android.util.Log
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_NOTICE_DIALOG
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ui.dialog.CommonNoticeDialog
import com.maunc.toolbox.databinding.ActivityFfmpegM3u8ToMp4Binding
import com.maunc.toolbox.ffmpeg.adapter.FFmpegM3u8ToMp4Adapter
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.data.FFmpegM3u8ResultData
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegM3u8ToMp4ViewModel

class FFmpegM3u8ToMp4Activity :
    BaseActivity<FFmpegM3u8ToMp4ViewModel, ActivityFfmpegM3u8ToMp4Binding>() {

    private val m3u8ResultAdapter by lazy {
        FFmpegM3u8ToMp4Adapter().apply {
            setOnM3u8ItemClickListener(object : FFmpegM3u8ToMp4Adapter.OnM3u8ItemClickListener {
                override fun convertItemClick(m3u8Data: FFmpegM3u8ResultData, pos: Int) {
                    mViewModel.transStatus.value = FFMPEG_START
                }

                override fun viewDetailsItemClick(m3u8Data: FFmpegM3u8ResultData, pos: Int) {

                }
            })
        }
    }

    private val tipsDialog by lazy {
        CommonNoticeDialog().setTitle(obtainString(R.string.ffmpeg_m3u8_to_mp4_tips_title_tv))
            .setContentText(obtainString(R.string.ffmpeg_m3u8_to_mp4_tips_content_tv))
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.ffmpegM3u8ToMp4ViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.ffmpeg_main_item_two)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_explanation)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            tipsDialog.show(supportFragmentManager, COMMON_NOTICE_DIALOG)
        }
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.ffmpegM3u8ToMp4Recycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegM3u8ToMp4Recycler.adapter = m3u8ResultAdapter
        mViewModel.initM3u8FileList()
    }

    override fun createObserver() {
        mViewModel.m3u8ResultList.observe(this) {
            mViewModel.isExistM3u8File.value = it.isNotEmpty()
            it.forEach { data ->
                Log.e("ww", "${data.totalSegmentCount},${data.isStandardM3u8}")
                data.segmentList.forEach { ts ->
                    Log.e("ww", "ts:${ts.index},${ts.duration},${ts.filePath}")
                }
            }
            m3u8ResultAdapter.setList(it)
        }
    }
}