package com.maunc.randomcallname.constant

//全局TAG
const val GLOBAL_TAG = "RandomCallNameApp"

//空字符串
const val GLOBAL_NONE_STRING = ""

//ActivityAnimDefault
const val ACTIVITY_ANIM_DEFAULT = 0

/**
 * 随机未开始状态 : RUN_STATUS_NONE
 * 随机开始状态  : RUN_STATUS_START
 * 随机暂停状态  : RUN_STATUS_STOP
 */
const val RUN_STATUS_NONE = 1
const val RUN_STATUS_START = 2
const val RUN_STATUS_STOP = 3

//随机点名的线程名称
const val TIME_THREAD_NAME = "timeThread"

//缩放动画键值
const val SCALE_X = "scaleX"
const val SCALE_Y = "scaleY"

//数据库名称
const val DATA_BASE_NAME = "random_call_name"

/**
 * 展示和收起键盘的间隔          : DELAY_SHOW_KEY_BROAD
 * 键盘顶起后延迟更改布局的时间   : DELAY_UPDATE_LAYOUT
 */
const val DELAY_KEY_BROAD = 100L
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

/**============================================== setResult来源值 ==============================================*/
//来源于  缺省值
const val RESULT_SOURCE_FROM_NONE_PAGE = -1
//来源于创建新分组页面
const val RESULT_SOURCE_FROM_NEW_GROUP_PAGE = 101
//来源于创建新名称页面
const val RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE = 102