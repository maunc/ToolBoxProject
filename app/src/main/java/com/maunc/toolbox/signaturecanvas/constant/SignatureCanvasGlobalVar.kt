package com.maunc.toolbox.signaturecanvas.constant

/**
 * 画笔或者橡皮
 */
const val MODE_PEN = 1
const val MODE_ERASER = 2

/**
 * 控制台点击
 */
const val SIGN_CONTROLLER_BACK = 1
const val SIGN_CONTROLLER_NEXT = 2
const val SIGN_CONTROLLER_PEN = 3
const val SIGN_CONTROLLER_ERASER = 4
const val SIGN_CONTROLLER_CLEAR = 5
const val SIGN_CONTROLLER_SAVE = 6

/**
 * 设置颜色的最大进度和最小进度
 */
const val RGB_SEEK_MIN_VALUE = 0
const val RGB_SEEK_MAX_VALUE = 255

/**
 * 画笔的最大宽度和最小宽度
 */
const val PEN_WIDTH_MIN_VALUE = 15
const val PEN_WIDTH_MAX_VALUE = 100

/**setResult来源**/
const val RESULT_SOURCE_FROM_SIGNATURE_CANVAS_SETTING = 101

/**intent跳转key值**/
const val INTENT_CANVAS_PEN_WIDTH = "intentCanvasPenWidth"
const val INTENT_CANVAS_PEN_COLOR = "intentCanvasPenColor"