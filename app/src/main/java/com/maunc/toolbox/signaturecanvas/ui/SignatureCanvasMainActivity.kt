package com.maunc.toolbox.signaturecanvas.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.databinding.ActivitySignatureCanvasMainBinding
import com.maunc.toolbox.signaturecanvas.viewmodel.SignatureCanvasMainViewModel

class SignatureCanvasMainActivity :
    BaseActivity<SignatureCanvasMainViewModel, ActivitySignatureCanvasMainBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.signature_canvas_title)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            startTargetActivity(SignatureCanvasSettingActivity::class.java)
        }
    }

    override fun createObserver() {

    }

}