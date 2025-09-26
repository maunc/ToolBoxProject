package com.maunc.toolbox.signaturecanvas.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemSignatureCanvasControllerBinding
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_BACK
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_CLEAR
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_ERASER
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_NEXT
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_PEN
import com.maunc.toolbox.signaturecanvas.data.SignatureCanvasControllerData

@SuppressLint("NotifyDataSetChanged")
class SignatureCanvasControllerAdapter :
    BaseQuickAdapter<SignatureCanvasControllerData, BaseDataBindingHolder<ItemSignatureCanvasControllerBinding>>(
        R.layout.item_signature_canvas_controller
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSignatureCanvasControllerBinding>,
        item: SignatureCanvasControllerData,
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
    }
}