package com.maunc.toolbox.signaturecanvas.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class CanvasSettingData(
    override val itemType: Int,
    var title: String,
    var isExpand: Boolean = false,
) : Serializable, MultiItemEntity {
    companion object {
        const val CANVAS_PEN_COLOR_TYPE = 0//画笔颜色配置
        const val CANVAS_PEN_WIDTH_TYPE = 1//画笔宽度配置
    }
}
