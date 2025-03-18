package com.maunc.toolbox.voicerecord.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.animateSetWidthAndHeight
import com.maunc.toolbox.commonbase.ext.checkPermissionAvailable
import com.maunc.toolbox.commonbase.ext.checkPermissionManualRequest
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.getDimens
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.screenHeight
import com.maunc.toolbox.commonbase.ext.setTint
import com.maunc.toolbox.commonbase.ext.startAppSystemSettingPage
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.databinding.ActivityVoiceRecordBinding
import com.maunc.toolbox.voicerecord.constant.AUDIO_PERMISSION_START_DIALOG
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_DOWN
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_MOVE_CANCEL
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_UP
import com.maunc.toolbox.voicerecord.viewmodel.VoiceRecordViewModel


class VoiceRecordActivity : BaseActivity<VoiceRecordViewModel, ActivityVoiceRecordBinding>() {

    companion object {
        const val ENLARGE_ANIM = 0 //执行扩大动画
        const val SHRINK_ANIM = 1 //执行缩小动画
    }

    private val requestAudioPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (!result) {
            if (checkPermissionManualRequest(Manifest.permission.RECORD_AUDIO)) {
                CommonDialog().setTitle(
                    getString(R.string.permission_manual_request_text)
                ).setSureListener {
                    startAppSystemSettingPage()
                }.setCancelListener {
                    toast(getString(R.string.permission_manual_request_text))
                }.show(supportFragmentManager, AUDIO_PERMISSION_START_DIALOG)
            }
        }
    }

    private var userDownY = 0

    private var percent15 = 15 / 100.0

    // 是否执行过扩大动画
    private var executeEnlargeAnim = false

    // 是否执行过缩小动画
    private var executeShrinkAnim = false

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.voiceRecordViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.tool_box_item_record_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mViewModel.createVoiceRecordConfig()
        mDatabind.voiceWaveView.start()
        mDatabind.voiceRecordStartButton.setOnTouchListener { v, event ->
            if (!checkPermissionAvailable(Manifest.permission.RECORD_AUDIO)) {
                requestAudioPermissionResult.launch(Manifest.permission.RECORD_AUDIO)
                return@setOnTouchListener false
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mViewModel.launchVibrator()
                    userDownY = event.rawY.toInt()
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_DOWN
                }

                MotionEvent.ACTION_UP -> {
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_UP
                    startCancelAnim(SHRINK_ANIM)
                }

                MotionEvent.ACTION_MOVE -> {
                    if (userDownY - event.rawY.toInt() > (screenHeight() * (percent15))) {
                        executeShrinkAnim = false
                        if (!executeEnlargeAnim) {
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL
                            executeEnlargeAnim = true
                            startCancelAnim(ENLARGE_ANIM)
                        }
                    } else {
                        executeEnlargeAnim = false
                        if (!executeShrinkAnim) {
                            executeShrinkAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
                            startCancelAnim(SHRINK_ANIM)
                        }
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun createObserver() {
        mViewModel.isVocals.observe(this) {
            if (it) {
                "说话了".loge()
            } else {
                "没有说话".loge()
            }
        }
    }

    private fun startCancelAnim(isEnlarge: Int) {
        mDatabind.voiceRecordUpCancelBg.animateSetWidthAndHeight(
            targetWidth =
            getDimens(
                if (isEnlarge == ENLARGE_ANIM) {
                    R.dimen.dp_150
                } else {
                    R.dimen.dp_100
                }
            ),
            targetHeight = getDimens(
                if (isEnlarge == ENLARGE_ANIM) {
                    R.dimen.dp_150
                } else {
                    R.dimen.dp_100
                }
            ),
            endListener = {
                mDatabind.voiceRecordUpCancelBg.setBackgroundResource(
                    if (isEnlarge == ENLARGE_ANIM) {
                        R.drawable.bg_red_oval
                    } else {
                        R.drawable.bg_gray_oval
                    }
                )
                mDatabind.voiceRecordUpCancelIcon.setTint(
                    if (isEnlarge == ENLARGE_ANIM) {
                        R.color.white
                    } else {
                        R.color.black
                    }
                )
            }
        )
    }
}
