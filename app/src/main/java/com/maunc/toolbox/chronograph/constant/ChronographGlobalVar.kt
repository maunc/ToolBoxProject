package com.maunc.toolbox.chronograph.constant

const val DELAY_MILLS = 10L
const val SPEED_NUM = 0.0117f
const val DEF_TIME_TEXT = "00:00.00"

const val CHRONOGRAPH_THREAD_NAME = "chronographThread"

/**
 * 计时未开始状态 : CHRONOGRAPH_STATUS_NONE
 * 计时开始状态  : CHRONOGRAPH_STATUS_START
 * 计时暂停状态  : CHRONOGRAPH_STATUS_STOP
 */
const val CHRONOGRAPH_STATUS_NONE = 1
const val CHRONOGRAPH_STATUS_START = 2
const val CHRONOGRAPH_STATUS_STOP = 3

/**
 * alpha
 */
//缩放动画键值
const val ALPHA = "alpha"
const val ALPHA_ZERO = 0f
const val ALPHA_ONE = 1f