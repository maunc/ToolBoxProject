package com.maunc.toolbox.commonbase.utils

import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
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

/** 随机名称用到的 */
//随机速度
const val randomSpeed = "randomSpeed" // 随机速度 在手动点名下生效
const val randomButtonClickVibrator = "randomButtonClickVibrator" //按钮点击震动
const val randomEggs = "randomEggs"
const val randomSelectRecyclerVisible = "randomSelectRecyclerVisible" //是否启用已点列表功能
const val randomType = "randomType"//点名类型
const val randomListSortType = "randomListSortType"//点名类型

/**颜色画板用到的**/
const val canvasPenColorA = "canvasPenColorA"
const val canvasPenColorR = "canvasPenColorR"
const val canvasPenColorG = "canvasPenColorG"
const val canvasPenColorB = "canvasPenColorB"

class MMKVUtils {

    fun init() {
        if (obtainMMKV.getBoolean(commonNotFirstLaunchApp)) {
            return
        }
        obtainMMKV.putBoolean(commonNotFirstLaunchApp, true)
        //随机名相关
        obtainMMKV.putLong(randomSpeed, RANDOM_SPEED_MAX)
        obtainMMKV.putBoolean(randomButtonClickVibrator, true)
        obtainMMKV.putBoolean(randomEggs, false)
        obtainMMKV.putBoolean(randomSelectRecyclerVisible, false)
        obtainMMKV.putInt(randomType, RANDOM_MANUAL)
        obtainMMKV.putInt(randomListSortType, RANDOM_DB_SORT_BY_INSERT_TIME_ASC)

        //画板相关
        obtainMMKV.putInt(canvasPenColorA, RGB_SEEK_MAX_VALUE)
        obtainMMKV.putInt(canvasPenColorR, RGB_SEEK_MIN_VALUE)
        obtainMMKV.putInt(canvasPenColorG, RGB_SEEK_MIN_VALUE)
        obtainMMKV.putInt(canvasPenColorB, RGB_SEEK_MIN_VALUE)
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
