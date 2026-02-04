package com.maunc.toolbox.signaturecanvas.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemCanvasControllerBinding
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_BACK
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_CLEAR
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_ERASER
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_NEXT
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_PEN
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_SAVE
import com.maunc.toolbox.signaturecanvas.data.CanvasControllerData

@SuppressLint("NotifyDataSetChanged")
class SignatureCanvasControllerAdapter :
    BaseQuickAdapter<CanvasControllerData, BaseDataBindingHolder<ItemCanvasControllerBinding>>(
        R.layout.item_canvas_controller
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCanvasControllerBinding>,
        item: CanvasControllerData,
    ) {
        holder.dataBinding?.let { dataBind ->
            dataBind.itemSignatureCanvasControllerImage.setImageResource(item.image)
            dataBind.itemSignatureCanvasControllerTitle.text = item.title
            dataBind.itemSignatureCanvasControllerRoot.setOnClickListener {
                when (item.type) {
                    SIGN_CONTROLLER_BACK -> mSignatureCanvasListener?.onBackListener()
                    SIGN_CONTROLLER_NEXT -> mSignatureCanvasListener?.onNextListener()
                    SIGN_CONTROLLER_PEN -> mSignatureCanvasListener?.onPenListener()
                    SIGN_CONTROLLER_ERASER -> mSignatureCanvasListener?.onEraserListener()
                    SIGN_CONTROLLER_CLEAR -> mSignatureCanvasListener?.onClearListener()
                    SIGN_CONTROLLER_SAVE -> mSignatureCanvasListener?.onSaveListener()
                }
            }
        }
    }

    private var mSignatureCanvasListener: SignatureCanvasControllerListener? = null

    fun setControllerListener(signatureCanvasControllerListener: SignatureCanvasControllerListener) {
        this.mSignatureCanvasListener = signatureCanvasControllerListener
    }

    interface SignatureCanvasControllerListener {
        fun onBackListener()
        fun onNextListener()
        fun onPenListener()
        fun onEraserListener()
        fun onClearListener()
        fun onSaveListener()
    }
}