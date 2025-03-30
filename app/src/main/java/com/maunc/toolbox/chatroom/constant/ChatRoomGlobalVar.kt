package com.maunc.toolbox.chatroom.constant

import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.obtainDimens

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

/**
 * 输入框高度enum
 */
var EDIT_NONE_LINE = obtainDimens(R.dimen.dp_50)
var EDIT_ONE_LINE = obtainDimens(R.dimen.dp_80)
var EDIT_TWO_LINE = obtainDimens(R.dimen.dp_110)
var EDIT_THREE_LINE = obtainDimens(R.dimen.dp_140)
var EDIT_FOUR_LINE = obtainDimens(R.dimen.dp_170)
var EDIT_FIVE_LINE = obtainDimens(R.dimen.dp_200)
var EDIT_SIX_LINE = obtainDimens(R.dimen.dp_230)

//只能选择一张照片发送
const val SEND_IMAGE_MAX_NUM = 3

/**============================================== 跳转IntentKey ==============================================*/
const val FULL_SCREEN_IMAGE_POS_EXTRA = "imagePositionExtra"
const val FULL_SCREEN_IMAGE_DATA_EXTRA = "imageDataExtra"

const val TEXT_CONTENT_DATA_EXTRA = "textDataExtra"
const val TEXT_SEND_TIME_DATA_EXTRA = "textDataSendTimeExtra"
