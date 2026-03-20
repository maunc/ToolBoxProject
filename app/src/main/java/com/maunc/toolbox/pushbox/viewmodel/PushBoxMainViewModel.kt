package com.maunc.toolbox.pushbox.viewmodel

import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.R
import com.maunc.toolbox.pushbox.data.PushBoxMainFunctionData

class PushBoxMainViewModel : BaseViewModel<BaseModel>() {

    //起始时间
    var startTimeValue: Long = 0L

    fun initFunctionList() = mutableListOf<PushBoxMainFunctionData>().mutableListInsert(
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_up_gradle_text),
            img = R.drawable.icon_ffmpeg_convert,
            backgroundRes = R.drawable.bg_radius_12_blue,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_UP_GRADE
        ),
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_next_gradle_text),
            img = R.drawable.icon_ffmpeg_convert,
            backgroundRes = R.drawable.bg_radius_12_blue,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_NEXT_GRADE
        ),
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_undo_text),
            img = R.drawable.icon_loading,
            backgroundRes = R.drawable.bg_radius_12_purple,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_UNDO_GRADE
        ),
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_re_start_text),
            img = R.drawable.icon_loading,
            backgroundRes = R.drawable.bg_radius_12_red,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_RESTART_GRADE
        ),
    )
}