package com.maunc.toolbox.randomname.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class RandomSettingData(
    override val itemType: Int,
    val settingType: String,
    var isExpand: Boolean = false,
) : Serializable, MultiItemEntity {
    companion object {
        const val RANDOM_SPEED_TYPE = 0// 速度配置
        const val RANDOM_BUTTON_VIBRATOR_TYPE = 1// 按钮震动配置
        const val RANDOM_BUTTON_EGGS_TYPE = 2// 彩蛋功能
        const val RANDOM_SELECT_LIST_TYPE = 3// 选择名单功能启用
        const val RANDOM_NAME_TYPE_TYPE = 4// 点名类型配置
        const val RANDOM_MANAGE_SORT_TYPE = 5 // asc升序,desc降序
        const val RANDOM_DELETE_ALL_DATA_TYPE = 6// 删除所有数据
        const val RANDOM_MANAGER_DATA_TYPE = 7 // 管理所有数据
        const val RANDOM_SELECT_DATA_TYPE = 8 // 选择分组
        const val RANDOM_RESULT_TEXT_BOLD_TYPE = 9 // 文本是否加粗
    }
}
