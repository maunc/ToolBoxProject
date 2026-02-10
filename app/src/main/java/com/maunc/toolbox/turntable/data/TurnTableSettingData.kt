package com.maunc.toolbox.turntable.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class TurnTableSettingData(
    override val itemType: Int,
    val settingType: String,
    var isExpand: Boolean = false,
) : Serializable, MultiItemEntity {
    companion object {
        const val TURN_TABLE_SELECT_DATA_TYPE = 0// 选择转盘数据
        const val TURN_TABLE_MANAGER_DATA_TYPE = 1// 管理转盘数据
        const val TURN_TABLE_DELETE_ALL_DATA_TYPE = 2// 删除所有数据
        const val TURN_TABLE_ANIM_INTERPOLATOR_TYPE = 3// 动画插值器分类
        const val TURN_TABLE_ENABLE_TOUCH_TYPE = 4// 是否启用转盘触摸
    }
}