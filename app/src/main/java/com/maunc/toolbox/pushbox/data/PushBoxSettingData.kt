package com.maunc.toolbox.pushbox.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class PushBoxSettingData(
    val title: String,
    var isExpand: Boolean = false,
    override val itemType: Int,
) : Serializable, MultiItemEntity {
    companion object {
        const val PUSH_BOX_MAP_PREVIEW_TYPE = 0//地图预览
        const val PUSH_BOX_CONTROLLER_SIZE = 1//方向键大小
        const val PUSH_BOX_TOUCH_VIEW = 2//是否可以触摸移动
    }
}
