package com.maunc.toolbox.signaturecanvas.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.addSeekBarListener
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.databinding.ActivitySignatureCanvasSettingBinding
import com.maunc.toolbox.signaturecanvas.viewmodel.SignatureCanvasSettingViewModel

@SuppressLint("SetTextI18n")
class SignatureCanvasSettingActivity :
    BaseActivity<SignatureCanvasSettingViewModel, ActivitySignatureCanvasSettingBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.signatureCanvasSettingViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.signature_canvas_setting_text)
        handlerColor()
        initSeekProgress()
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.signatureCanvasRColorSeek.addSeekBarListener(onProgressChanged = { _, progress, fromUser ->
            mDatabind.signatureCanvasRColorValue.text = "$progress"
            mViewModel.rValue = progress
            if (fromUser) handlerColor()
        })
        mDatabind.signatureCanvasGColorSeek.addSeekBarListener(onProgressChanged = { _, progress, fromUser ->
            mDatabind.signatureCanvasGColorValue.text = "$progress"
            mViewModel.gValue = progress
            if (fromUser) handlerColor()
        })
        mDatabind.signatureCanvasBColorSeek.addSeekBarListener(onProgressChanged = { _, progress, fromUser ->
            mDatabind.signatureCanvasBColorValue.text = "$progress"
            mViewModel.bValue = progress
            if (fromUser) handlerColor()
        })
        mDatabind.signatureCanvasAColorSeek.addSeekBarListener(onProgressChanged = { _, progress, fromUser ->
            mDatabind.signatureCanvasAColorValue.text = "$progress"
            mViewModel.aValue = progress
            if (fromUser) handlerColor()
        })
    }

    override fun createObserver() {}

    private fun initSeekProgress() {
        mDatabind.signatureCanvasAColorSeek.apply {
            max = mViewModel.rgbSeekMaxValue
            progress = mViewModel.aValue
        }
        mDatabind.signatureCanvasRColorSeek.apply {
            max = mViewModel.rgbSeekMaxValue
            progress = mViewModel.rValue
        }
        mDatabind.signatureCanvasGColorSeek.apply {
            max = mViewModel.rgbSeekMaxValue
            progress = mViewModel.gValue
        }
        mDatabind.signatureCanvasBColorSeek.apply {
            max = mViewModel.rgbSeekMaxValue
            progress = mViewModel.bValue
        }
        mDatabind.signatureCanvasAColorValue.text = "${mViewModel.aValue}"
        mDatabind.signatureCanvasRColorValue.text = "${mViewModel.rValue}"
        mDatabind.signatureCanvasGColorValue.text = "${mViewModel.gValue}"
        mDatabind.signatureCanvasBColorValue.text = "${mViewModel.bValue}"
    }

    private fun handlerColor() {
        val colorARGB = colorARGB()
        mDatabind.signatureCanvasSettingColorText.text = colorARGBtoString(colorARGB)
        mDatabind.signatureCanvasSettingColor.setBackgroundColor(colorARGB)
    }

    private fun colorARGB(
        a: Int = mViewModel.aValue,
        r: Int = mViewModel.rValue,
        g: Int = mViewModel.gValue,
        b: Int = mViewModel.bValue,
    ) = Color.argb(a, r, g, b)

    private fun colorARGBtoString(
        colorInt: Int,
    ) = "#${colorInt.toUInt().toString(16).uppercase()}"
}