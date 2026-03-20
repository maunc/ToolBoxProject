package com.maunc.toolbox.commonbase.ui.dialog

import android.os.Bundle
import com.maunc.base.ext.showSoftInputKeyBoard
import com.maunc.base.ui.BaseDialog
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.viewmodel.CommonEditDataDialogViewModel
import com.maunc.toolbox.databinding.DialogCommonEditDataBinding

class CommonEditDataDialog :
    BaseDialog<CommonEditDataDialogViewModel, DialogCommonEditDataBinding>() {

    private var onSureListener: CommonEditDataDialogOnSureListener? = null
    private var onCancelListener: CommonEditDataDialogOnCancelListener? = null
    private var onDismissListener: CommonEditDataDialogOnDismissListener? = null
    private var prefixString = GLOBAL_NONE_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        showSoftInputKeyBoard(mDatabind.dialogCommonEditView)
        mDatabind.dialogCommonEditDataSureButton.setOnClickListener {
            val editContent = mDatabind.dialogCommonEditView.text?.toString()
            if (editContent?.isEmpty() == true) {
                onSureListener?.onSure(GLOBAL_NONE_STRING)
            } else {
                onSureListener?.onSure(prefixString + editContent)
            }
            dismissAllowingStateLoss()
        }
        mDatabind.dialogCommonEditDataCancelButton.setOnClickListener {
            onCancelListener?.onCancel()
            dismissAllowingStateLoss()
        }
        mDatabind.dialogCommonEditDataRoot.setOnClickListener {
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

    fun setPrefixString(prefix: String): CommonEditDataDialog {
        this.prefixString = prefix
        return this
    }

    fun setSureListener(onSureListener: CommonEditDataDialogOnSureListener): CommonEditDataDialog {
        this.onSureListener = onSureListener
        return this
    }

    fun setCancelListener(onCancelListener: CommonEditDataDialogOnCancelListener): CommonEditDataDialog {
        this.onCancelListener = onCancelListener
        return this
    }

    fun setDismissListener(onDismissListener: CommonEditDataDialogOnDismissListener): CommonEditDataDialog {
        this.onDismissListener = onDismissListener
        return this
    }

    fun interface CommonEditDataDialogOnSureListener {
        fun onSure(content: String)
    }

    fun interface CommonEditDataDialogOnCancelListener {
        fun onCancel()
    }

    fun interface CommonEditDataDialogOnDismissListener {
        fun onDismiss()
    }
}