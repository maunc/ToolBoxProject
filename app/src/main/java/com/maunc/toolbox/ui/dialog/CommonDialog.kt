package com.maunc.toolbox.ui.dialog

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.base.BaseDialog
import com.maunc.toolbox.databinding.DialogCommonBinding
import com.maunc.toolbox.viewmodel.CommonDialogViewModel

class CommonDialog : BaseDialog<CommonDialogViewModel, DialogCommonBinding>() {

    private var titleText: String = ""
    private var onSureListener: CommonDialogOnSureListener? = null
    private var onCancelListener: CommonDialogOnCancelListener? = null
    private var onDismissListener: CommonDialogOnDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.dialogCommonTitle.text = titleText
        mDatabind.dialogCommonSureButton.setOnClickListener {
            onSureListener?.onSure()
            dismissAllowingStateLoss()
        }
        mDatabind.dialogCommonCancelButton.setOnClickListener {
            onCancelListener?.onCancel()
            dismissAllowingStateLoss()
        }
        mDatabind.dialogCommonRoot.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss()
        onDismissListener?.onDismiss()
    }

    override fun dismiss() {
        super.dismiss()
        onDismissListener?.onDismiss()
    }

    fun setTitle(title: String): CommonDialog {
        this.titleText = title
        return this
    }

    fun setSureListener(onSureListener: CommonDialogOnSureListener): CommonDialog {
        this.onSureListener = onSureListener
        return this
    }

    fun setCancelListener(onCancelListener: CommonDialogOnCancelListener): CommonDialog {
        this.onCancelListener = onCancelListener
        return this
    }

    fun setDismissListener(onDismissListener: CommonDialogOnDismissListener): CommonDialog {
        this.onDismissListener = onDismissListener
        return this
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }

    fun interface CommonDialogOnSureListener {
        fun onSure()
    }

    fun interface CommonDialogOnCancelListener {
        fun onCancel()
    }

    fun interface CommonDialogOnDismissListener {
        fun onDismiss()
    }
}