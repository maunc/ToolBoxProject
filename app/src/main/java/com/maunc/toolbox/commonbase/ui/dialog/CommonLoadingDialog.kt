package com.maunc.toolbox.commonbase.ui.dialog

import android.os.Bundle
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

    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }
}