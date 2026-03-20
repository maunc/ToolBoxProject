package com.maunc.toolbox.signaturecanvas.viewmodel

import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.R
import com.maunc.toolbox.signaturecanvas.data.CanvasSettingData

class SignatureCanvasSettingViewModel : BaseViewModel<BaseModel>() {

    var settingDataList = mutableListOf<CanvasSettingData>().apply {
        mutableListInsert(
            CanvasSettingData(
                title = obtainString(R.string.signature_canvas_setting_pen_width_text),
                itemType = CanvasSettingData.CANVAS_PEN_WIDTH_TYPE
            ),
            CanvasSettingData(
                title = obtainString(R.string.signature_canvas_setting_eraser_width_text),
                itemType = CanvasSettingData.CANVAS_ERASER_WIDTH_TYPE
            ),
            CanvasSettingData(
                title = obtainString(R.string.signature_canvas_setting_pen_color_text),
                itemType = CanvasSettingData.CANVAS_PEN_COLOR_TYPE
            ),
            CanvasSettingData(
                title = obtainString(R.string.signature_canvas_setting_manager_file_text),
                itemType = CanvasSettingData.CANVAS_FILE_MANAGE
            )
        )
    }
}