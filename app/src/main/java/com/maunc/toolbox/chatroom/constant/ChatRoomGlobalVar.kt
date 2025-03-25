package com.maunc.toolbox.chatroom.constant

const val AUDIO_PERMISSION_START_DIALOG = "startSystemPageAudioDialog"

//震动时间
const val DEFAULT_VIBRATOR_TIME = 15L

//震动幅度
const val DEFAULT_RECORD_TOUCH_AMPLITUDE = 2

/**
 * 录音View状态
 */
const val RECORD_VIEW_STATUS_DOWN = 0 //按下
const val RECORD_VIEW_STATUS_UP = 1 //抬起
const val RECORD_VIEW_STATUS_MOVE_CANCEL = 2 //移动到可取消
const val RECORD_VIEW_STATUS_MOVE_CANCEL_DONE = 3 //移动过可取消后返回继续录制

/**
 * 聊天模式
 */
const val CHAT_ROOM_TEXT_TYPE = 1
const val CHAT_ROOM_RECORD_TYPE = 2