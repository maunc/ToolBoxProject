package com.maunc.toolbox.voicerecord.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.animateSetWidthAndHeight
import com.maunc.toolbox.commonbase.ext.checkPermissionAvailable
import com.maunc.toolbox.commonbase.ext.checkPermissionManualRequest
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.getDimens
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.obtainActivityIntent
import com.maunc.toolbox.commonbase.ext.screenHeight
import com.maunc.toolbox.commonbase.ext.screenWidth
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

@SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
class VoiceRecordActivity : BaseActivity<VoiceRecordViewModel, ActivityVoiceRecordBinding>() {

    companion object {
        const val ENLARGE_ANIM = 0 //执行扩大动画
        const val SHRINK_ANIM = 1 //执行缩小动画

        const val CONTROLLER_CANCEL_VIEW = 0
        const val CONTROLLER_SURE_VIEW = 1
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

    private var userDownX = 0

    private var percent12 = 12 / 100.0
    private var percent50 = 50 / 100.0

    // 是否执行过取消扩大动画
    private var executeCancelEnlargeAnim = false

    // 是否执行过取消缩小动画
    private var executeCancelShrinkAnim = false

    //是否执行过确定扩大动画
    private var executeSureEnlargeAnim = false

    //是否执行过确定缩小动画
    private var executeSureShrinkAnim = false

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.voiceRecordViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.tool_box_item_record_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }

        mDatabind.playMusic.clickScale {

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
                    userDownX = event.rawX.toInt()
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_DOWN
                    mViewModel.startRecordVoice()
                    mViewModel.isWriteWavHeader.postValue(true)
                }

                MotionEvent.ACTION_UP -> {
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_UP
                    startScaleAnim(SHRINK_ANIM, CONTROLLER_CANCEL_VIEW)
                    startScaleAnim(SHRINK_ANIM, CONTROLLER_SURE_VIEW)
                    mViewModel.stopRecordVoice()
                }

                MotionEvent.ACTION_MOVE -> {
                    // 未滑动到可以操作按钮范围
                    if (userDownY - event.rawY.toInt() < (screenHeight() * (percent12))) {
                        executeCancelEnlargeAnim = false
                        executeSureEnlargeAnim = false
                        if (!executeCancelShrinkAnim) {
                            executeCancelShrinkAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_CANCEL_VIEW)
                            mViewModel.isWriteWavHeader.postValue(true)
                        }
                        if (!executeSureShrinkAnim) {
                            executeSureShrinkAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_SURE_VIEW)
                        }
                        return@setOnTouchListener true
                    }
                    // 选中sure
                    if (event.rawX.toInt() > (screenWidth() * percent50)) {
                        if (!executeSureEnlargeAnim) {
                            executeSureEnlargeAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
                            startScaleAnim(ENLARGE_ANIM, CONTROLLER_SURE_VIEW)
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_CANCEL_VIEW)
                            mViewModel.isWriteWavHeader.postValue(true)
                        }
                    } else {
                        //选中cancel
                        if (!executeCancelEnlargeAnim) {
                            executeCancelEnlargeAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL
                            startScaleAnim(ENLARGE_ANIM, CONTROLLER_CANCEL_VIEW)
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_SURE_VIEW)
                            mViewModel.isWriteWavHeader.postValue(false)
                        }
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun createObserver() {
    }

    private fun startScaleAnim(
        animType: Int, viewType: Int,
        action: () -> Unit = {
            when (viewType) {
                CONTROLLER_SURE_VIEW -> {
                    executeSureShrinkAnim = false
                    executeSureEnlargeAnim = false
                }

                CONTROLLER_CANCEL_VIEW -> {
                    executeCancelShrinkAnim = false
                    executeCancelEnlargeAnim = false
                }
            }
        },
    ) {
        when (viewType) {
            CONTROLLER_SURE_VIEW -> {
                if (animType == ENLARGE_ANIM) {
                    mDatabind.voiceRecordUpSureBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_120),
                        targetHeight = getDimens(R.dimen.dp_120)
                    ) {
                        action()
                        mDatabind.voiceRecordUpSureBg.setBackgroundResource(
                            R.drawable.bg_record_controller_sure_oval
                        )
                        mDatabind.voiceRecordUpSureIcon.setTint(R.color.white)
                    }
                }
                if (animType == SHRINK_ANIM) {
                    mDatabind.voiceRecordUpSureBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_100),
                        targetHeight = getDimens(R.dimen.dp_100)
                    ) {
                        action()
                        mDatabind.voiceRecordUpSureBg.setBackgroundResource(
                            R.drawable.bg_record_controller_none_oval
                        )
                        mDatabind.voiceRecordUpSureIcon.setTint(R.color.black)
                    }
                }
            }

            CONTROLLER_CANCEL_VIEW -> {
                if (animType == ENLARGE_ANIM) {
                    mDatabind.voiceRecordUpCancelBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_120),
                        targetHeight = getDimens(R.dimen.dp_120)
                    ) {
                        action()
                        mDatabind.voiceRecordUpCancelBg.setBackgroundResource(
                            R.drawable.bg_record_controller_cancel_oval
                        )
                        mDatabind.voiceRecordUpCancelIcon.setTint(R.color.white)
                    }
                }
                if (animType == SHRINK_ANIM) {
                    mDatabind.voiceRecordUpCancelBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_100),
                        targetHeight = getDimens(R.dimen.dp_100)
                    ) {
                        action()
                        mDatabind.voiceRecordUpCancelBg.setBackgroundResource(
                            R.drawable.bg_record_controller_none_oval
                        )
                        mDatabind.voiceRecordUpCancelIcon.setTint(R.color.black)
                    }
                }
            }
        }
    }
}
