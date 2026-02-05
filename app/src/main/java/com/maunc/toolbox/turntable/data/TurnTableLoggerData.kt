package com.maunc.toolbox.turntable.data

import com.chad.library.adapter.base.entity.MultiItemEntity

data class TurnTableLoggerData(
    val time: Long = System.currentTimeMillis(),
    val result: String,
    override val itemType: Int,
) : MultiItemEntity {
    companion object {
        const val TURN_TABLE_TIPS_LOG = 1 // 提示型log
        const val TURN_TABLE_RESULT_LOG = 2 // 结果log
    }
}
