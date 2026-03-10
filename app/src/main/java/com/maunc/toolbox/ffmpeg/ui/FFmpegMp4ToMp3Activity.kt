package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_LOADING_DIALOG
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.ui.dialog.CommonLoadingDialog
import com.maunc.toolbox.commonbase.utils.createFileDir
import com.maunc.toolbox.databinding.ActivityFfmpegMp4ToMp3Binding
import com.maunc.toolbox.ffmpeg.adapter.FFmpegMp4ToMp3MsgAdapter
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_CREATE_DIR_TAG
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.MP4_TO_MP3_SAVE_PATH_NAME
import com.maunc.toolbox.ffmpeg.constant.SAVE_ROOT_PATH
import com.maunc.toolbox.ffmpeg.data.FFmpegMp4ToMp3MsgData
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegMp4ToMp3ViewModel

class FFmpegMp4ToMp3Activity :
    BaseActivity<FFmpegMp4ToMp3ViewModel, ActivityFfmpegMp4ToMp3Binding>() {

    private val currentMp4MsgAdapter by lazy {
        FFmpegMp4ToMp3MsgAdapter()
    }

    private val loadingDialog by lazy {
        CommonLoadingDialog()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.ffmpegMp4ToMp3ViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.ffmpeg_main_item_one)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.ffmpegMp4ToMp3SelectMp4FileTv.clickScale {
            mViewModel.startSelectMp4File(this)
        }
        mDatabind.ffmpegMp4ToMp3HandleMp4FileToTv.clickScale {
            mViewModel.currentSelectMp4File.value?.let {
                mViewModel.startTransformation()
            } ?: mViewModel.startSelectMp4File(this)
        }
        mDatabind.ffmpegMp4ToMp3TargetFileMsgRecycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegMp4ToMp3TargetFileMsgRecycler.adapter = currentMp4MsgAdapter
    }

    override fun createObserver() {
        mViewModel.currentSelectMp4File.observe(this) {
            it?.let {
                currentMp4MsgAdapter.setList(mutableListOf<FFmpegMp4ToMp3MsgData>().apply {
                    mutableListInsert(
                        FFmpegMp4ToMp3MsgData(
                            obtainString(R.string.ffmpeg_mp4_to_mp3_file_path_tips), it.realPath
                        ),
                        FFmpegMp4ToMp3MsgData(
                            obtainString(R.string.ffmpeg_mp4_to_mp3_to_end_path_tips),
                            mViewModel.obtainToMp3Path(it.realPath) ?: GLOBAL_NONE_STRING
                        )
                    )
                })
                mDatabind.ffmpegMp4ToMp3VideoPlayer.setNewVideoPlay(videoUrl = it.realPath)
            }
        }
        mViewModel.transStatus.observe(this) {
            when (it) {
                FFMPEG_START -> loadingDialog.show(
                    supportFragmentManager, COMMON_LOADING_DIALOG
                )

                FFMPEG_SUCCESS -> {
                    taskEndCallback(R.string.ffmpeg_success_tv)
                    mViewModel.currentSelectMp4File.value = null
                    mDatabind.ffmpegMp4ToMp3VideoPlayer.onDestroy()
                }

                FFMPEG_ERROR -> taskEndCallback(R.string.ffmpeg_error_tv)
            }
        }
    }

    private fun taskEndCallback(@StringRes toastMsgResId: Int) {
        loadingDialog.dismissAllowingStateLoss()
        toast(obtainString(toastMsgResId))
    }

    override fun onPause() {
        super.onPause()
        mDatabind.ffmpegMp4ToMp3VideoPlayer.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabind.ffmpegMp4ToMp3VideoPlayer.onDestroy()
    }
}