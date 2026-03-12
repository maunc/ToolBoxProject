package com.maunc.toolbox.pushbox.viewmodel

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.pushbox.data.PushBoxSettingData

class PushBoxSettingViewModel : BaseViewModel<BaseModel>() {
    var settingDataList = mutableListOf<PushBoxSettingData>().mutableListInsert(
        PushBoxSettingData(
            title = obtainString(R.string.push_box_setting_preview_text),
            itemType = PushBoxSettingData.PUSH_BOX_MAP_PREVIEW_TYPE
        ),
        PushBoxSettingData(
            title = obtainString(R.string.push_box_setting_controller_size_text),
            itemType = PushBoxSettingData.PUSH_BOX_CONTROLLER_SIZE
        ),
        PushBoxSettingData(
            title = obtainString(R.string.push_box_setting_touch_move_text),
            itemType = PushBoxSettingData.PUSH_BOX_TOUCH_VIEW
        ),
    )
}