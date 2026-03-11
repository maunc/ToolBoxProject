package com.maunc.toolbox.pushbox.data

import androidx.annotation.DrawableRes

data class PushBoxMainFunctionData(
    val title: String,
    @DrawableRes val img: Int,
    val functionType: Int,
) {
    companion object {
        const val PUSH_BOX_MAIN_FUNCTION_UP_GRADE = 1//上一关
        const val PUSH_BOX_MAIN_FUNCTION_NEXT_GRADE = 2//下一关
        const val PUSH_BOX_MAIN_FUNCTION_UNDO_GRADE = 3//回退
        const val PUSH_BOX_MAIN_FUNCTION_RESTART_GRADE = 4//重置当前关卡
    }
}
