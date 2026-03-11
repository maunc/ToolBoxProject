package com.maunc.toolbox.pushbox.data

import com.maunc.toolbox.pushbox.constant.PushBoxMoveDirection

/**
 * 移动过的步骤记录
 */
data class PushBoxStepRecord(
    val mapState: Array<IntArray>, // 移动后的地图状态
    val manX: Int, // 人物X坐标
    val manY: Int, // 人物Y坐标
    val moveDirection: PushBoxMoveDirection, // 移动方向
    val isPushBox: Boolean, // 是否推箱子
) {
    // 重写equals和hashCode，避免Array比较的问题
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PushBoxStepRecord

        if (!mapState.contentDeepEquals(other.mapState)) return false
        if (manX != other.manX) return false
        if (manY != other.manY) return false
        if (moveDirection != other.moveDirection) return false
        if (isPushBox != other.isPushBox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mapState.contentDeepHashCode()
        result = 31 * result + manX
        result = 31 * result + manY
        result = 31 * result + moveDirection.hashCode()
        result = 31 * result + isPushBox.hashCode()
        return result
    }
}
