package com.maunc.toolbox.voicerecord.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.checkPermissionAvailable
import com.maunc.toolbox.commonbase.ext.checkPermissionManualRequest
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.startAppSystemSettingPage
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.databinding.ActivityVoiceRecordBinding
import com.maunc.toolbox.voicerecord.constant.AUDIO_PERMISSION_START_DIALOG
import com.maunc.toolbox.voicerecord.viewmodel.VoiceRecordViewModel


class VoiceRecordActivity : BaseActivity<VoiceRecordViewModel, ActivityVoiceRecordBinding>() {

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

    @SuppressLint("ClickableViewAccessibility")
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
                    "start button down event".loge()
                    mViewModel.launchVibrator()
                    mViewModel.recordButtonTips.value = getString(R.string.voice_record_up_tips)
                }

                MotionEvent.ACTION_UP -> {
                    "start button UP event".loge()
                    mViewModel.recordButtonTips.value = getString(R.string.voice_record_down_tips)
                }

                MotionEvent.ACTION_MOVE -> {
                    "start button MOVE event".loge()
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
}
