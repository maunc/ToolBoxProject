package com.maunc.toolbox.randomname.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class RandomSettingData(
    override val itemType: Int,
    val settingType: String,
    var isExpand: Boolean = false,
) : Serializable, MultiItemEntity {
    companion object {
        const val RANDOM_SLEEP_TYPE = 0
        const val RANDOM_BUTTON_CLICK_VIBRATOR_TYPE = 1
    }
}
