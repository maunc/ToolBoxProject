package com.maunc.toolbox.signaturecanvas.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class CanvasSettingData(
    override val itemType: Int,
    var title: String,
    var isExpand: Boolean = false,
) : Serializable, MultiItemEntity {
    companion object {
        const val CANVAS_PEN_WIDTH_TYPE = 0//画笔宽度配置
        const val CANVAS_ERASER_WIDTH_TYPE = 1//橡皮大小配置
        const val CANVAS_PEN_COLOR_TYPE = 2//画笔颜色配置
        const val CANVAS_FILE_MANAGE = 3//文件管理页
    }
}
