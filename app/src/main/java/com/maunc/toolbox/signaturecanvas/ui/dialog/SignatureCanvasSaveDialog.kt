package com.maunc.toolbox.signaturecanvas.ui.dialog

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseDialog
import com.maunc.toolbox.commonbase.ext.showSoftInputKeyBoard
import com.maunc.toolbox.databinding.DialogSignatureCanvasSaveBinding
import com.maunc.toolbox.signaturecanvas.viewmodel.SignatureCanvasSaveViewModel

class SignatureCanvasSaveDialog :
    BaseDialog<SignatureCanvasSaveViewModel, DialogSignatureCanvasSaveBinding>() {

    private var onSureListener: CanvasSaveDialogOnSureListener? = null
    private var onCancelListener: CanvasSaveDialogOnCancelListener? = null
    private var onDismissListener: CanvasSaveDialogOnDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        showSoftInputKeyBoard(mDatabind.dialogCanvasSaveEditView)
        mDatabind.dialogCanvasSaveSureButton.setOnClickListener {
            onSureListener?.onSure()
            dismissAllowingStateLoss()
        }
        mDatabind.dialogCanvasSaveCancelButton.setOnClickListener {
            onCancelListener?.onCancel()
            dismissAllowingStateLoss()
        }
        mDatabind.dialogCanvasSaveRoot.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }

    override fun dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss()
        onDismissListener?.onDismiss()
    }

    override fun dismiss() {
        super.dismiss()
        onDismissListener?.onDismiss()
    }

    fun setSureListener(onSureListener: CanvasSaveDialogOnSureListener): SignatureCanvasSaveDialog {
        this.onSureListener = onSureListener
        return this
    }

    fun setCancelListener(onCancelListener: CanvasSaveDialogOnCancelListener): SignatureCanvasSaveDialog {
        this.onCancelListener = onCancelListener
        return this
    }

    fun setDismissListener(onDismissListener: CanvasSaveDialogOnDismissListener): SignatureCanvasSaveDialog {
        this.onDismissListener = onDismissListener
        return this
    }

    fun interface CanvasSaveDialogOnSureListener {
        fun onSure()
    }

    fun interface CanvasSaveDialogOnCancelListener {
        fun onCancel()
    }

    fun interface CanvasSaveDialogOnDismissListener {
        fun onDismiss()
    }

}