package com.maunc.toolbox.signaturecanvas.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.signaturecanvas.constant.MODE_PEN
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_BACK
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_CLEAR
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_ERASER
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_NEXT
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_PEN
import com.maunc.toolbox.signaturecanvas.data.SignatureCanvasControllerData

class SignatureCanvasMainViewModel : BaseViewModel<BaseModel>() {

    //当前画笔模式
    var drawModel = MutableLiveData(MODE_PEN)

    var controllerDataList = mutableListOf<SignatureCanvasControllerData>().apply {
        add(
            SignatureCanvasControllerData(
                image = R.drawable.icon_signature_canvas_back,
                title = obtainString(R.string.signature_canvas_back_tv),
                type = SIGN_CONTROLLER_BACK
            )
        )
        add(
            SignatureCanvasControllerData(
                image = R.drawable.icon_signature_canvas_next,
                title = obtainString(R.string.signature_canvas_next_tv),
                type = SIGN_CONTROLLER_NEXT
            )
        )
        add(
            SignatureCanvasControllerData(
                image = R.drawable.icon_signature_canvas_pen,
                title = obtainString(R.string.signature_canvas_pen_tv),
                type = SIGN_CONTROLLER_PEN
            )
        )
        add(
            SignatureCanvasControllerData(
                image = R.drawable.icon_signature_canvas_eraser,
                title = obtainString(R.string.signature_canvas_eraser_tv),
                type = SIGN_CONTROLLER_ERASER
            )
        )
        add(
            SignatureCanvasControllerData(
                image = R.drawable.icon_signature_canvas_clear,
                title = obtainString(R.string.signature_canvas_clear_tv),
                type = SIGN_CONTROLLER_CLEAR
            )
        )
    }
}