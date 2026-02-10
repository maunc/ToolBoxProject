package com.maunc.toolbox.turntable.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class TurnTableEditData(
    var content: String,
    override val itemType: Int,
    // 这里加一个time是为了区分不同的对象
    // data class 本身已经重写的hashCode 如果传入的所有值都一样则hashCode就一样
    var time: Long = System.currentTimeMillis(),
) : Serializable, MultiItemEntity {
    companion object {
        const val EDIT_TURN_TABLE_TITLE = 0
        const val EDIT_TURN_TABLE_NAME = 1
    }
}
