package com.maunc.toolbox.signaturecanvas.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.canvasEraserWidth
import com.maunc.toolbox.commonbase.utils.canvasPenWidth
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.signaturecanvas.constant.MODE_PEN
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_BACK
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_CLEAR
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_ERASER
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_NEXT
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_PEN
import com.maunc.toolbox.signaturecanvas.constant.SIGN_CONTROLLER_SAVE
import com.maunc.toolbox.signaturecanvas.data.CanvasControllerData

class SignatureCanvasMainViewModel : BaseViewModel<BaseModel>() {
    //当前画笔模式
    var drawModel = MutableLiveData(MODE_PEN)

    //画笔宽度
    var mCanvasPenWidth = MutableLiveData(obtainMMKV.getInt(canvasPenWidth))

    //橡皮大小
    var mCanvasEraserWidth = MutableLiveData(obtainMMKV.getInt(canvasEraserWidth))

    //画笔颜色
    var mCanvasPenColor = MutableLiveData<Int>()

    var controllerDataList = mutableListOf<CanvasControllerData>().apply {
        mutableListInsert(
            CanvasControllerData(
                image = R.drawable.icon_signature_canvas_back,
                title = obtainString(R.string.signature_canvas_back_tv),
                type = SIGN_CONTROLLER_BACK
            ),
            CanvasControllerData(
                image = R.drawable.icon_signature_canvas_next,
                title = obtainString(R.string.signature_canvas_next_tv),
                type = SIGN_CONTROLLER_NEXT
            ),
            CanvasControllerData(
                image = R.drawable.icon_signature_canvas_pen,
                title = obtainString(R.string.signature_canvas_pen_tv),
                type = SIGN_CONTROLLER_PEN
            ),
            CanvasControllerData(
                image = R.drawable.icon_signature_canvas_eraser,
                title = obtainString(R.string.signature_canvas_eraser_tv),
                type = SIGN_CONTROLLER_ERASER
            ),
            CanvasControllerData(
                image = R.drawable.icon_signature_canvas_clear,
                title = obtainString(R.string.signature_canvas_clear_tv),
                type = SIGN_CONTROLLER_CLEAR
            ),
            CanvasControllerData(
                image = R.drawable.icon_signature_canvas_save,
                title = obtainString(R.string.signature_canvas_save_tv),
                type = SIGN_CONTROLLER_SAVE
            ),
        )
    }
}