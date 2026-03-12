package com.maunc.toolbox.pushbox.viewmodel

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.pushbox.data.PushBoxMainFunctionData

class PushBoxMainViewModel : BaseViewModel<BaseModel>() {

    fun initFunctionList() = mutableListOf<PushBoxMainFunctionData>().mutableListInsert(
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_up_gradle_text),
            img = R.drawable.icon_ffmpeg_convert,
            backgroundRes = R.drawable.bg_blue_radius_12,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_UP_GRADE
        ),
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_next_gradle_text),
            img = R.drawable.icon_ffmpeg_convert,
            backgroundRes = R.drawable.bg_blue_radius_12,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_NEXT_GRADE
        ),
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_undo_text),
            img = R.drawable.icon_new_loading,
            backgroundRes = R.drawable.bg_purple_radius_12,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_UNDO_GRADE
        ),
        PushBoxMainFunctionData(
            title = obtainString(R.string.push_box_main_re_start_text),
            img = R.drawable.icon_new_loading,
            backgroundRes = R.drawable.bg_red_radius_12,
            functionType = PushBoxMainFunctionData.PUSH_BOX_MAIN_FUNCTION_RESTART_GRADE
        ),
    )
}