package com.maunc.toolbox.signaturecanvas.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.finishCurrentResultToActivity
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ActivitySignatureCanvasSettingBinding
import com.maunc.toolbox.signaturecanvas.adapter.SignatureCanvasSettingAdapter
import com.maunc.toolbox.signaturecanvas.constant.RESULT_SOURCE_FROM_SIGNATURE_CANVAS_SETTING
import com.maunc.toolbox.signaturecanvas.viewmodel.SignatureCanvasSettingViewModel

@SuppressLint("SetTextI18n")
class SignatureCanvasSettingActivity :
    BaseActivity<SignatureCanvasSettingViewModel, ActivitySignatureCanvasSettingBinding>() {

    private val signatureCanvasSettingAdapter by lazy {
        SignatureCanvasSettingAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.signatureCanvasSettingViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.signature_canvas_setting_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            baseFinishCurrentActivity()
        }
        mDatabind.signatureCanvasSettingRecycler.layoutManager = linearLayoutManager()
        mDatabind.signatureCanvasSettingRecycler.adapter = signatureCanvasSettingAdapter
        signatureCanvasSettingAdapter.setList(mViewModel.settingDataList)
    }

    override fun onBackPressCallBack() {
        baseFinishCurrentActivity()
    }

    override fun createObserver() {}

    private fun baseFinishCurrentActivity(action: () -> Unit = {}) {
        action()
        finishCurrentResultToActivity(
            resultCode = RESULT_SOURCE_FROM_SIGNATURE_CANVAS_SETTING
        )
    }
}