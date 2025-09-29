package com.maunc.toolbox.signaturecanvas.databindadapter

import androidx.databinding.BindingAdapter
import com.us.mauncview.SignatureView

object SignatureCanvasDataBindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["handleSignatureCanvasPenWidth"], requireAll = false)
    fun handleSignatureCanvasPenWidth(signatureCanvasView: SignatureView, penWidth: Float) {
        signatureCanvasView.setPenSize(penWidth)
    }
}