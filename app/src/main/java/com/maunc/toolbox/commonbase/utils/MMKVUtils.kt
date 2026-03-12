package com.maunc.toolbox.commonbase.utils

import com.maunc.toolbox.pushbox.constant.PUSH_BOX_CONTROLLER_SIZE_MEDIUM
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RANDOM_RESULT_TEXT_SIZE_DEFAULT_VALUE
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.signaturecanvas.constant.RGB_SEEK_MAX_VALUE
import com.maunc.toolbox.signaturecanvas.constant.RGB_SEEK_MIN_VALUE
import com.tencent.mmkv.MMKV

val obtainMMKV: MMKVUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    MMKVUtils()
}

//默认值
private const val DEFAULT_STRING = ""
private const val DEFAULT_INT = -1

/**通用*/
const val commonNotFirstLaunchApp = "notFirstLaunchApp"

const val buttonClickVibrator = "buttonClickVibrator" //按钮点击震动

/** 随机名称用到的 */
const val randomSpeed = "randomSpeed" // 随机速度 在手动点名下生效

const val randomTextBold = "randomTextBold" //结果文本加粗
const val randomTextSize = "randomTextSize" //结果文本大小
const val randomRepeat = "randomRepeat" //是否允许重复点名
const val randomSelectRecyclerVisible = "randomSelectRecyclerVisible" //是否启用已点列表功能
const val randomType = "randomType"//点名类型
const val randomListSortType = "randomListSortType"//点名类型
const val randomEnumCountEnableType = "randomEnumCountEnableType"//点名统计
const val randomEggs = "randomEggs"

/**颜色画板用到的**/
const val canvasPenColorA = "canvasPenColorA"
const val canvasPenColorR = "canvasPenColorR"
const val canvasPenColorG = "canvasPenColorG"
const val canvasPenColorB = "canvasPenColorB"
const val canvasPenWidth = "canvasPenWidth" //画笔宽度
const val canvasEraserWidth = "canvasEraserWidth" //橡皮宽度

/**转盘用到的*/
const val turnTableEnableTouch = "turnTableEnableTouch"
const val turnTableAnimSoundEffect = "turnTableAnimSoundEffect"
const val turnTableConfigColor = "turnTableConfigColor"

/**推箱子用到的*/
const val pushBoxControllerButtonSize = "pushBoxControllerSize"
const val pushBoxTouchView = "pushBoxTouchView"

class MMKVUtils {

    fun init() {
        if (obtainMMKV.getBoolean(commonNotFirstLaunchApp)) {
            return
        }
        obtainMMKV.putBoolean(commonNotFirstLaunchApp, true)
        //随机名相关
        obtainMMKV.putLong(randomSpeed, RANDOM_SPEED_MAX)
        obtainMMKV.putBoolean(buttonClickVibrator, false)
        obtainMMKV.putBoolean(randomTextBold, false)
        obtainMMKV.putBoolean(randomEggs, false)
        obtainMMKV.putBoolean(randomRepeat, false)
        obtainMMKV.putBoolean(randomSelectRecyclerVisible, false)
        obtainMMKV.putBoolean(randomEnumCountEnableType, false)
        obtainMMKV.putInt(randomTextSize, RANDOM_RESULT_TEXT_SIZE_DEFAULT_VALUE)
        obtainMMKV.putInt(randomType, RANDOM_MANUAL)
        obtainMMKV.putInt(randomListSortType, RANDOM_DB_SORT_BY_INSERT_TIME_ASC)

        //画板相关
        obtainMMKV.putInt(canvasPenColorA, RGB_SEEK_MAX_VALUE)
        obtainMMKV.putInt(canvasPenColorR, RGB_SEEK_MIN_VALUE)
        obtainMMKV.putInt(canvasPenColorG, RGB_SEEK_MIN_VALUE)
        obtainMMKV.putInt(canvasPenColorB, RGB_SEEK_MIN_VALUE)
        obtainMMKV.putInt(canvasPenWidth, 40)
        obtainMMKV.putInt(canvasEraserWidth, 40)

        //转盘相关
        obtainMMKV.putBoolean(turnTableEnableTouch, false)
        obtainMMKV.putBoolean(turnTableAnimSoundEffect, false)
        obtainMMKV.putInt(turnTableConfigColor, 0)

        //推箱子相关
        obtainMMKV.putInt(pushBoxControllerButtonSize, PUSH_BOX_CONTROLLER_SIZE_MEDIUM)
        obtainMMKV.putBoolean(pushBoxTouchView, false)
    }

    fun getString(
        key: String,
    ): String? = MMKV.defaultMMKV().decodeString(key, DEFAULT_STRING)

    fun getInt(
        key: String,
    ): Int = MMKV.defaultMMKV().decodeInt(key, DEFAULT_INT)

    fun getLong(
        key: String,
    ): Long = MMKV.defaultMMKV().decodeLong(key)

    fun getBoolean(
        key: String,
    ): Boolean = MMKV.defaultMMKV().decodeBool(key, false)

    fun putLong(
        key: String,
        value: Long,
    ) = MMKV.defaultMMKV().encode(key, value)

    fun putBoolean(
        key: String,
        value: Boolean,
    ) = MMKV.defaultMMKV().encode(key, value)

    fun putString(
        key: String,
        value: String,
    ) = MMKV.defaultMMKV().encode(key, value)

    fun putInt(
        key: String,
        value: Int,
    ) = MMKV.defaultMMKV().encode(key, value)
}
