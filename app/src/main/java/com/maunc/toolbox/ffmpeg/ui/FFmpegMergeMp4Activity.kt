package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import androidx.annotation.StringRes
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_LOADING_DIALOG
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.toast
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
        FFmpegMergeMp4Adapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                mViewModel.playCurrentIndex = pos
                playMp4FileAndHandleRecycler(pos, data[pos].realPath)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.ffmpegMergeMp4ViewModel = mViewModel
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
            rePlayerResourceAndRecycler()
            mViewModel.startMergeMp4List()
        }
        mDatabind.ffmpegMergeMp4DataRecycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegMergeMp4DataRecycler.adapter = ffmpegMergeMp4Adapter
        mDatabind.ffmpegMergeMp4Player.setOnCompleteListener {
            mViewModel.playCurrentIndex += 1
            if (mViewModel.playCurrentIndex > (mViewModel.targetSelectList.value?.size ?: 1) - 1) {
                rePlayerResourceAndRecycler()
                return@setOnCompleteListener
            }
            playMp4FileAndHandleRecycler(
                mViewModel.playCurrentIndex,
                ffmpegMergeMp4Adapter.data[mViewModel.playCurrentIndex].realPath
            )
        }
    }

    override fun createObserver() {
        mViewModel.targetSelectList.observe(this) {
            if (it.isNullOrEmpty()) {
                rePlayerResourceAndRecycler()
                return@observe
            }
            rePlayerResourceAndRecycler()
            ffmpegMergeMp4Adapter.setList(it)
            playMp4FileAndHandleRecycler(
                mViewModel.playCurrentIndex,
                ffmpegMergeMp4Adapter.data[mViewModel.playCurrentIndex].realPath
            )
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

    private fun playMp4FileAndHandleRecycler(pos: Int, path: String) {
        ffmpegMergeMp4Adapter.selectMp4(pos)
        mDatabind.ffmpegMergeMp4Player.setNewVideoPlay(path).playVideoPlay()
    }

    private fun taskEndCallback(@StringRes toastMsgResId: Int) {
        rePlayerResourceAndRecycler()
        mViewModel.currentSelectList.clear()
        mViewModel.targetSelectList.value = null
        loadingDialog.dismissAllowingStateLoss()
        toast(obtainString(toastMsgResId))
    }

    /**
     * 重置布局和释放播放器资源
     */
    private fun rePlayerResourceAndRecycler() {
        mViewModel.playCurrentIndex = 0
        ffmpegMergeMp4Adapter.selectMp4(-1)
        mDatabind.ffmpegMergeMp4Player.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mDatabind.ffmpegMergeMp4Player.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabind.ffmpegMergeMp4Player.onDestroy()
    }
}