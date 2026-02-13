package com.maunc.toolbox.commonbase.ui.dialog

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseDialog
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.viewmodel.CommonDialogViewModel
import com.maunc.toolbox.databinding.DialogCommonBinding

class CommonDialog : BaseDialog<CommonDialogViewModel, DialogCommonBinding>() {

    private var titleText: String = obtainString(R.string.functions_are_under_development)
    private var sureText: String = obtainString(R.string.common_dialog_sure_text)
    private var cancelText: String = obtainString(R.string.common_dialog_cancel_text)
    private var onSureListener: CommonDialogOnSureListener? = null
    private var onCancelListener: CommonDialogOnCancelListener? = null
    private var onDismissListener: CommonDialogOnDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.dialogCommonTitle.text = titleText
        mDatabind.dialogCommonSureButton.text = sureText
        mDatabind.dialogCommonCancelButton.text = cancelText
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

    fun setSureText(text: String): CommonDialog {
        this.sureText = text
        return this
    }

    fun setCancelText(text: String): CommonDialog {
        this.cancelText = text
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