package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import androidx.annotation.StringRes
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.luck.picture.lib.thread.PictureThreadUtils
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_LOADING_DIALOG
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.gone
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.logd
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.ext.visible
import com.maunc.toolbox.commonbase.ui.dialog.CommonLoadingDialog
import com.maunc.toolbox.databinding.ActivityFfmpegMergeMp4Binding
import com.maunc.toolbox.ffmpeg.adapter.FFmpegMergeMp4Adapter
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegMergeMp4ViewModel

class FFmpegMergeMp4Activity :
    BaseActivity<FFmpegMergeMp4ViewModel, ActivityFfmpegMergeMp4Binding>() {

    private val loadingDialog by lazy {
        CommonLoadingDialog()
    }
        
    private val ffmpegMergeMp4Adapter by lazy {
        FFmpegMergeMp4Adapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.ffmpeg_main_item_four)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_add)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            mViewModel.startSelectMp4File(this)
        }
        mDatabind.ffmpegMergeMp4SelectFileTv.clickScale {
            mViewModel.startSelectMp4File(this)
        }
        mDatabind.ffmpegMergeMp4FileMergeTv.clickScale {
            if ((mViewModel.targetSelectList.value ?: mutableListOf()).isEmpty()) {
                mViewModel.startSelectMp4File(this)
                return@clickScale
            }
            mViewModel.startMergeMp4List()
        }
        mDatabind.ffmpegMergeMp4DataRecycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegMergeMp4DataRecycler.adapter = ffmpegMergeMp4Adapter
        mDatabind.itemFfmpegMergeMp4Player.setOnCompleteListener {

        }
        FFmpegKit.executeAsync("-i /storage/emulated/0/HToolBox/merge_mp4/normal_1773139237290.mp4 -hide_banner",{ session: FFmpegSession ->
            PictureThreadUtils.runOnUiThread {
                val returnCode = session.returnCode
                if (ReturnCode.isSuccess(returnCode)) {
                } else if (ReturnCode.isCancel(returnCode)) {
                } else {
                }
            }
        }, { log ->
            // 实时日志回调（可用于解析进度）
            "FFmpeg日志：${log.message}".logd()
        }, { statistics ->
            // 统计信息回调（如帧率、比特率等）
            "统计信息：$statistics".logd()
        })

        FFmpegKit.executeAsync("-i /storage/emulated/0/HToolBox/merge_mp4/normal_1773139244861.mp4 -hide_banner",{ session: FFmpegSession ->
            PictureThreadUtils.runOnUiThread {
                val returnCode = session.returnCode
                if (ReturnCode.isSuccess(returnCode)) {
                } else if (ReturnCode.isCancel(returnCode)) {
                } else {
                }
            }
        }, { log ->
            // 实时日志回调（可用于解析进度）
            "FFmpeg日志：${log.message}".logd()
        }, { statistics ->
            // 统计信息回调（如帧率、比特率等）
            "统计信息：$statistics".logd()
        })
    }

    override fun createObserver() {
        mViewModel.targetSelectList.observe(this) {
            if (!it.isNullOrEmpty()) {
                ffmpegMergeMp4Adapter.setList(it)
                mDatabind.ffmpegMergeMp4SelectLayout.visible()
                mDatabind.ffmpegMergeMp4NotSelectLayout.gone()
            }
        }
        mViewModel.transStatus.observe(this) {
            when (it) {
                FFMPEG_START -> loadingDialog.show(
                    supportFragmentManager,
                    COMMON_LOADING_DIALOG
                )

                FFMPEG_SUCCESS -> {
                    taskEndCallback(R.string.ffmpeg_success_tv)
                }

                FFMPEG_ERROR -> taskEndCallback(R.string.ffmpeg_error_tv)
            }
        }
    }

    private fun taskEndCallback(@StringRes toastMsgResId: Int) {
        loadingDialog.dismissAllowingStateLoss()
        toast(obtainString(toastMsgResId))
    }
}