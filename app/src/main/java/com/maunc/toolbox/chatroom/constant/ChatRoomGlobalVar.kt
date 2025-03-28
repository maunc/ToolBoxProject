package com.maunc.toolbox.chatroom.constant

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.getDimens

const val AUDIO_PERMISSION_START_DIALOG = "startSystemPageAudioDialog"

//整体布局更新速度
const val CHAT_ROOM_LAYOUT_UPDATE_TIME = 100L

//百分比
const val PERCENT_TWELVE = 12 / 100.0
const val PERCENT_FIFTY = 50 / 100.0

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

//30秒
const val THIRTY_SECOND = 30000

//60秒
const val SIXTY_SECOND = 60000

/**
 * SimpleDate
 */
const val YYYY_MM_DD_HH_MM_SS = "yyyy年MM月dd日 HH:mm:ss"

/**
 * 输入框高度enum
 */
var EDIT_NONE_LINE = getDimens(R.dimen.dp_50)
var EDIT_ONE_LINE = getDimens(R.dimen.dp_80)
var EDIT_TWO_LINE = getDimens(R.dimen.dp_110)
var EDIT_THREE_LINE = getDimens(R.dimen.dp_140)
var EDIT_FOUR_LINE = getDimens(R.dimen.dp_170)
var EDIT_FIVE_LINE = getDimens(R.dimen.dp_200)
