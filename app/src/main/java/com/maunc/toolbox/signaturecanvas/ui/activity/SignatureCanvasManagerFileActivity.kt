package com.maunc.toolbox.signaturecanvas.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivitySignatureCanvasManagerFileBinding
import com.maunc.toolbox.signaturecanvas.viewmodel.SignatureCanvasManagerFileViewModel

class SignatureCanvasManagerFileActivity :
    BaseActivity<SignatureCanvasManagerFileViewModel, ActivitySignatureCanvasManagerFileBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.signature_canvas_setting_manager_file_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mViewModel.initSaveFile()
    }

    override fun createObserver() {

    }

}