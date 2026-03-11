package com.maunc.toolbox.commonbase.ui.dialog

import android.os.Bundle
import android.view.KeyEvent
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseDialog
import com.maunc.toolbox.commonbase.viewmodel.CommonLoadingDialogViewModel
import com.maunc.toolbox.databinding.DialogCommonLoadingBinding

class CommonLoadingDialog : BaseDialog<CommonLoadingDialogViewModel, DialogCommonLoadingBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 拦截返回键
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            return@setOnKeyListener keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }
}