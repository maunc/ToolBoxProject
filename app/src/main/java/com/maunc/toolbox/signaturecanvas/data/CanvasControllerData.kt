package com.maunc.toolbox.signaturecanvas.data

import androidx.annotation.DrawableRes

data class CanvasControllerData(
    @DrawableRes
    var image: Int,
    var title: String,
    var type: Int,
    var isSelect: Boolean = false,
)
