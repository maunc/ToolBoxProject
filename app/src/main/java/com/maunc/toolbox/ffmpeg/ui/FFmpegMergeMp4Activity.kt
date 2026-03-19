package com.maunc.toolbox.ffmpeg.ui

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.annotation.StringRes
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_EDIT_DATA_DIALOG
import com.maunc.toolbox.commonbase.constant.COMMON_LOADING_DIALOG
import com.maunc.toolbox.commonbase.ext.addItemTouchHelper
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainDrawable
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.setScale
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.ui.dialog.CommonEditDataDialog
import com.maunc.toolbox.commonbase.ui.dialog.CommonLoadingDialog
import com.maunc.toolbox.databinding.ActivityFfmpegMergeMp4Binding
import com.maunc.toolbox.ffmpeg.adapter.FFmpegMergeMp4Adapter
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_ERROR
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_START
import com.maunc.toolbox.ffmpeg.constant.FFMPEG_SUCCESS
import com.maunc.toolbox.ffmpeg.constant.SAVE_FFMPEG_PREFIX
import com.maunc.toolbox.ffmpeg.viewmodel.FFmpegMergeMp4ViewModel
import java.util.Collections

class FFmpegMergeMp4Activity :
    BaseActivity<FFmpegMergeMp4ViewModel, ActivityFfmpegMergeMp4Binding>() {
    private var dragTargetPosition = 0

    private val loadingDialog by lazy {
        CommonLoadingDialog()
    }

    private val commonEditDataDialog by lazy {
        CommonEditDataDialog().setPrefixString(SAVE_FFMPEG_PREFIX).setSureListener { fileName ->
            if (fileName.isEmpty()) {
                toast(getString(R.string.common_dialog_edit_data_tips))
                return@setSureListener
            }
            rePlayerResourceAndRecycler()
            mViewModel.startMergeMp4List(fileName)
        }
    }

    private val ffmpegMergeMp4Adapter by lazy {
        FFmpegMergeMp4Adapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                if (pos == obtainCurrentSelectPos()) return@setOnItemClickListener
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
            commonEditDataDialog.show(supportFragmentManager, COMMON_EDIT_DATA_DIALOG)
        }
        mDatabind.ffmpegMergeMp4DataRecycler.layoutManager = linearLayoutManager()
        mDatabind.ffmpegMergeMp4DataRecycler.adapter = ffmpegMergeMp4Adapter
        mDatabind.ffmpegMergeMp4DataRecycler.addItemTouchHelper(
            onMove = { fromPosition, toPosition ->
                dragTargetPosition = toPosition
                val currentList = ffmpegMergeMp4Adapter.data
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(currentList, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(currentList, i, i - 1)
                    }
                }
                ffmpegMergeMp4Adapter.notifyItemMoved(fromPosition, toPosition)
                true
            },
            onSelectedChanged = { viewHolder, actionState ->
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder?.itemView?.setScale(1.05f, 1.05f)
                    viewHolder?.itemView?.background =
                        obtainDrawable(R.drawable.stroke_black_bg_gray_radius_24)
                }
            },
            onClearView = { recyclerView, viewHolder ->
                if (!recyclerView.isComputingLayout) {
                    // 拖拽结束后同步顺序到 ViewModel，确保播放与合并顺序一致
                    val sortedList = ffmpegMergeMp4Adapter.data.toMutableList()
                    mViewModel.playCurrentIndex =
                        dragTargetPosition.coerceIn(0, (sortedList.size - 1).coerceAtLeast(0))
                    mViewModel.currentSelectList.clear()
                    mViewModel.currentSelectList.addAll(sortedList)
                    mViewModel.targetSelectList.value = sortedList
                    viewHolder.itemView.setScale(1.0f, 1.0f)
                    viewHolder.itemView.background =
                        obtainDrawable(R.drawable.bg_item_tool_box_selector)
                }
            }
        )
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
            mDatabind.ffmpegMergeMp4Player.onDestroy()
            ffmpegMergeMp4Adapter.setList(it)
            mViewModel.playCurrentIndex =
                mViewModel.playCurrentIndex.coerceIn(0, ffmpegMergeMp4Adapter.data.lastIndex)
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