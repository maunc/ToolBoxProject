package com.maunc.toolbox.pushbox.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class PushBoxSettingData(
    val title: String,
    var isExpand: Boolean = false,
    override val itemType: Int,
) : Serializable, MultiItemEntity {
    companion object {
        const val PUSH_BOX_MAP_PREVIEW = 0//地图预览
    }
}
