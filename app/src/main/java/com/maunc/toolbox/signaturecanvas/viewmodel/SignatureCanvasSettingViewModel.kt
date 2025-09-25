package com.maunc.toolbox.signaturecanvas.viewmodel

import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel

class SignatureCanvasSettingViewModel : BaseViewModel<BaseModel>() {

    val rgbSeekMinValue = 0
    val rgbSeekMaxValue = 255

    var aValue = 255
    var rValue = 0
    var gValue = 0
    var bValue = 0
}