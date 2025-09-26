package com.maunc.toolbox.signaturecanvas.data

import androidx.annotation.ColorRes

data class CanvasSettingColorData(
    var colorType: Int,
    var title: String,
    var seekValue: Int,
    @ColorRes
    var progressTint: Int,
    @ColorRes
    var progressBackgroundTint: Int,
) {
    companion object {
        const val A_COLOR_TYPE = 1
        const val R_COLOR_TYPE = 2
        const val G_COLOR_TYPE = 3
        const val B_COLOR_TYPE = 4
    }
}