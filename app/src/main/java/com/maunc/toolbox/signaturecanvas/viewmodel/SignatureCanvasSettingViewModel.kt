package com.maunc.toolbox.signaturecanvas.viewmodel

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingData
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingData.Companion.CANVAS_PEN_COLOR_TYPE
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingData.Companion.CANVAS_PEN_WIDTH_TYPE

class SignatureCanvasSettingViewModel : BaseViewModel<BaseModel>() {

    var settingDataList = mutableListOf<CanvasSettingData>().apply {
        mutableListInsert(
            CanvasSettingData(
                title = obtainString(R.string.signature_canvas_setting_pen_width_text),
                itemType = CANVAS_PEN_WIDTH_TYPE
            ),
            CanvasSettingData(
                title = obtainString(R.string.signature_canvas_setting_pen_color_text),
                itemType = CANVAS_PEN_COLOR_TYPE
            ),
        )
    }
}