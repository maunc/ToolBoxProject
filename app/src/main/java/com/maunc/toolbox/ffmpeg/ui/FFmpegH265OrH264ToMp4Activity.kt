package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import androidx.annotation.StringRes
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.obtainString
import com.maunc.base.ext.toast
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.COMMON_LOADING_DIALOG
import com.maunc.toolbox.commonbase.ui.dialog.CommonLoadingDialog
import com.maunc.toolbox.databinding.ActivityFfmpegH265OrH264ToMp4Binding
import com.maunc.toolbox.ffmpeg.adapter.FFmpegH265OrH264ToMp4Adapter
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.data.FFmpegH265OrH264ToMp4ResultData
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegH265OrH264ToMp4ViewModel

class FFmpegH265OrH264ToMp4Activity :
    BaseActivity<FFmpegH265OrH264ToMp4ViewModel, ActivityFfmpegH265OrH264ToMp4Binding>() {

    private val loadingDialog by lazy {
        CommonLoadingDialog()
    }

    private val ffmpegH265OrH264ToMp4Adapter by lazy {
        FFmpegH265OrH264ToMp4Adapter().apply {
            setOnH265OrH264ItemClickListener(object :
                FFmpegH265OrH264ToMp4Adapter.OnH265OrH264ItemClickListener {
                override fun convertItemClick(fileData: FFmpegH265OrH264ToMp4ResultData, pos: Int) {
                    mViewModel.startTransformation(fileData)
                }

                override fun viewDetailsItemClick(
                    fileData: FFmpegH265OrH264ToMp4ResultData, pos: Int,
                ) {

                }
            })
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.ffmpegH265OrH264ToMp4ViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.ffmpeg_main_item_three)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.ffmpegH265OrH264ToMp4Recycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegH265OrH264ToMp4Recycler.adapter = ffmpegH265OrH264ToMp4Adapter
        mViewModel.initH265AndH264FileList()
    }

    override fun createObserver() {
        mViewModel.resultList.observe(this) {
            mViewModel.isExistFile.value = it.isNotEmpty()
            if (it.isNullOrEmpty()) return@observe
            ffmpegH265OrH264ToMp4Adapter.setList(it)
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

    private fun taskEndCallback(@StringRes toastMsgResId: Int) {
        loadingDialog.dismissAllowingStateLoss()
        toast(obtainString(toastMsgResId))
    }
}