package com.maunc.toolbox.randomname.constant

import android.media.MediaPlayer
import com.maunc.toolbox.R
import com.maunc.toolbox.ToolBoxApplication

/**
 * 随机未开始状态 : RUN_STATUS_NONE
 * 随机开始状态  : RUN_STATUS_START
 * 随机暂停状态  : RUN_STATUS_STOP
 */
const val RUN_STATUS_NONE = 1
const val RUN_STATUS_START = 2
const val RUN_STATUS_STOP = 3

//随机点名的线程名称
const val RANDOM_NAME_THREAD_NAME = "randomThread"

//按钮点击的音效
val mediaPlayer: MediaPlayer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    MediaPlayer.create(ToolBoxApplication.app, R.raw.random_button_click)
}

/**
 * 键盘顶起后延迟更改布局的时间
 */
const val DELAY_UPDATE_LAYOUT = 100L

//进入随机名称前过滤分组中名称小于等于该数值的分组
const val GROUP_REMOVE_THRESHOLD = 1

/**============================================== Dialog名称 ==============================================*/
//选择分组dialog
const val SELECT_GROUP_TO_MAIN_DIALOG = "selectGroupToMainDialog"

//公共Dialog
const val COMMON_DIALOG = "commonDialog"

/**============================================== 跳转IntentKey ==============================================*/
const val GROUP_NAME_EXTRA = "GroupName"
const val GROUP_WITH_NAME_EXTRA = "RandomGroupWithName"

//管理名称页面,新增名称页面(是否更改过数据库操作)
const val WHETHER_DATA_HAS_CHANGE = "whetherDataHasChange"

/**============================================== setResult来源值 ==============================================*/
//来源于  缺省值
const val RESULT_SOURCE_FROM_NONE_PAGE = -1

//来源于创建新分组页面
const val RESULT_SOURCE_FROM_NEW_GROUP_PAGE = 101

//来源于创建新名称页面
const val RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE = 102

//来源于分组名称管理页面
const val RESULT_SOURCE_FROM_MANAGE_GROUP_WITH_NAME_PAGE = 103