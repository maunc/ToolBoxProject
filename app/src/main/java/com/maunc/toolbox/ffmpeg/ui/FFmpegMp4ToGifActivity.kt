package com.maunc.toolbox.ffmpeg.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.StringRes
import com.google.android.material.slider.RangeSlider
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ext.toast
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.COMMON_LOADING_DIALOG
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ui.dialog.CommonLoadingDialog
import com.maunc.toolbox.databinding.ActivityFfmpegMp4ToGifBinding
import com.maunc.toolbox.ffmpeg.adapter.FFmpegMp4ToGifMsgAdapter
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.stringForTime
import com.maunc.toolbox.ffmpeg.data.FFmpegMp4ToGifMsgData
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegMp4ToGifViewModel

class FFmpegMp4ToGifActivity :
    BaseActivity<FFmpegMp4ToGifViewModel, ActivityFfmpegMp4ToGifBinding>() {
    private val currentMp4MsgAdapter by lazy {
        FFmpegMp4ToGifMsgAdapter()
    }

    private val loadingDialog by lazy {
        CommonLoadingDialog()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.ffmpegMp4ToGifViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.ffmpeg_main_item_five)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.ffmpegMp4ToGifSelectMp4FileTv.clickScale {
            mViewModel.startSelectMp4File(this)
        }
        mDatabind.ffmpegMp4ToGifHandleMp4FileToTv.clickScale {
            mViewModel.currentSelectMp4File.value?.let {
                mViewModel.startTransformation()
            } ?: mViewModel.startSelectMp4File(this)
        }
        mDatabind.ffmpegMp4ToGifTargetFileMsgRecycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegMp4ToGifTargetFileMsgRecycler.adapter = currentMp4MsgAdapter
        initCropView()
    }

    override fun createObserver() {
        mViewModel.currentSelectMp4File.observe(this) {
            it?.let {
                mDatabind.ffmpegMp4ToGifVideoPlayer.setNewVideoPlay(it.realPath).playVideoPlay()
                updateMsgRecycler()
            }
        }
        mViewModel.videoDurationMs.observe(this) { durationMs ->
            val maxSec = (durationMs / 1000L).toInt().coerceAtLeast(1)
            mDatabind.ffmpegMp4ToGifClipRangeSeek.valueFrom = 0f
            mDatabind.ffmpegMp4ToGifClipRangeSeek.valueTo = maxSec.toFloat()
            mDatabind.ffmpegMp4ToGifClipRangeSeek.values = listOf(0f, maxSec.toFloat())
            mViewModel.updateClipStartMs(0L)
            mViewModel.updateClipEndMs(durationMs)
            updateCropText()
            updateMsgRecycler()
        }
        mViewModel.clipStartMs.observe(this) {
            updateCropText()
            updateMsgRecycler()
        }
        mViewModel.clipEndMs.observe(this) {
            updateCropText()
            updateMsgRecycler()
        }
        mViewModel.transStatus.observe(this) {
            when (it) {
                FFMPEG_START -> loadingDialog.show(
                    supportFragmentManager, COMMON_LOADING_DIALOG
                )
                FFMPEG_SUCCESS -> taskEndCallback(R.string.ffmpeg_success_tv)
                FFMPEG_ERROR -> taskEndCallback(R.string.ffmpeg_error_tv)
            }
        }
    }

    private fun initCropView() {
        mDatabind.ffmpegMp4ToGifClipRangeSeek.addOnChangeListener(
            RangeSlider.OnChangeListener { slider, _, fromUser ->
                if (!fromUser) return@OnChangeListener
                val values = slider.values
                if (values.size < 2) return@OnChangeListener
                val startMs = (values[0] * 1000L).toLong()
                val endMs = (values[1] * 1000L).toLong()
                mViewModel.updateClipStartMs(startMs)
                mViewModel.updateClipEndMs(endMs)
            }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateCropText() {
        val startMs = mViewModel.clipStartMs.value ?: 0L
        val endMs = mViewModel.clipEndMs.value ?: 0L
        mDatabind.ffmpegMp4ToGifClipStartTv.text =
            "${obtainString(R.string.ffmpeg_mp4_to_gif_clip_start_tv)}${stringForTime(startMs)}"
        mDatabind.ffmpegMp4ToGifClipEndTv.text =
            "${obtainString(R.string.ffmpeg_mp4_to_gif_clip_end_tv)}${stringForTime(endMs)}"
    }

    private fun updateMsgRecycler() {
        val currentFile = mViewModel.currentSelectMp4File.value ?: return
        val startMs = mViewModel.clipStartMs.value ?: 0L
        val endMs = mViewModel.clipEndMs.value ?: 0L
        currentMp4MsgAdapter.setList(
            mutableListOf<FFmpegMp4ToGifMsgData>().mutableListInsert(
                FFmpegMp4ToGifMsgData(
                    obtainString(R.string.ffmpeg_mp4_to_gif_file_path_tips), currentFile.realPath
                ),
                FFmpegMp4ToGifMsgData(
                    obtainString(R.string.ffmpeg_mp4_to_gif_to_end_path_tips),
                    mViewModel.obtainToGifPath(currentFile.realPath) ?: GLOBAL_NONE_STRING
                ),
                FFmpegMp4ToGifMsgData(
                    obtainString(R.string.ffmpeg_mp4_to_gif_clip_range_tips),
                    "${stringForTime(startMs)} - ${stringForTime(endMs)}"
                )
            )
        )
    }

    private fun taskEndCallback(@StringRes toastMsgResId: Int) {
        mViewModel.currentSelectMp4File.value = null
        mDatabind.ffmpegMp4ToGifVideoPlayer.onDestroy()
        loadingDialog.dismissAllowingStateLoss()
        toast(obtainString(toastMsgResId))
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabind.ffmpegMp4ToGifVideoPlayer.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mDatabind.ffmpegMp4ToGifVideoPlayer.onPause()
    }
}

