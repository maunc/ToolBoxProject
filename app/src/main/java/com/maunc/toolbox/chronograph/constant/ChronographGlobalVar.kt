package com.maunc.toolbox.chronograph.constant

import android.annotation.SuppressLint

const val DELAY_MILLS = 10L
const val DEF_TIME_TEXT = "00:00.00"

/**
 * 计时未开始状态 : CHRONOGRAPH_STATUS_NONE
 * 计时开始状态  : CHRONOGRAPH_STATUS_START
 * 计时暂停状态  : CHRONOGRAPH_STATUS_PAUSE
 */
const val CHRONOGRAPH_STATUS_NONE = 1
const val CHRONOGRAPH_STATUS_START = 2
const val CHRONOGRAPH_STATUS_PAUSE = 3

/**
 * 转化为分:秒:毫秒
 */
@SuppressLint("DefaultLocale")
fun Long.timeUnitMillion(): String {
    val minutes = (this / 60000) % 100
    val seconds = (this % 60000) / 1000
    val millis = (this % 1000) / 10
    return String.format("%02d:%02d:%02d", minutes, seconds, millis)
}