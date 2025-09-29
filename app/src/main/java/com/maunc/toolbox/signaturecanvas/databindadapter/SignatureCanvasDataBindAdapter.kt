package com.maunc.toolbox.signaturecanvas.databindadapter

import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import com.us.mauncview.SignatureView

object SignatureCanvasDataBindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["handleSignatureCanvasPenWidth"], requireAll = false)
    fun handleSignatureCanvasPenWidth(signatureCanvasView: SignatureView, penWidth: Float) {
        signatureCanvasView.setPenSize(penWidth)
    }

    @JvmStatic
    @BindingAdapter(value = ["handleSignatureCanvasEraserWidth"], requireAll = false)
    fun handleSignatureCanvasEraserWidth(signatureCanvasView: SignatureView, eraserWidth: Float) {
        signatureCanvasView.setEraserSize(eraserWidth)
    }

    @JvmStatic
    @BindingAdapter(value = ["handleSignatureCanvasPenColor"], requireAll = false)
    fun handleSignatureCanvasPenColor(signatureCanvasView: SignatureView, @ColorRes penColor: Int) {
        signatureCanvasView.setPenColor(penColor)
    }
}