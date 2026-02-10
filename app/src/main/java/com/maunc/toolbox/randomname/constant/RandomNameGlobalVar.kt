package com.maunc.toolbox.randomname.constant

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

/**
 * 键盘顶起后延迟更改布局的时间
 */
const val DELAY_UPDATE_LAYOUT = 100L

/**
 * 三种点名类型
 */
const val RANDOM_NOW = 0
const val RANDOM_AUTO = 1
const val RANDOM_MANUAL = 2

/**
 * 三个档位速度
 */
const val RANDOM_SPEED_MIN = 320L
const val RANDOM_SPEED_MEDIUM = 120L
const val RANDOM_SPEED_MAX = 20L

/**
 * 三个文本大小
 */
const val RESULT_TEXT_SIZE_MIN = 0
const val RESULT_TEXT_SIZE_MEDIUM = 1
const val RESULT_TEXT_SIZE_MAX = 2

/**
 * 数据库排序方式
 */
const val RANDOM_DB_SORT_BY_INSERT_TIME_ASC = 0
const val RANDOM_DB_SORT_BY_INSERT_TIME_DESC = 1
const val RANDOM_DB_SORT_BY_NAME_ASC = 2
const val RANDOM_DB_SORT_BY_NAME_DESC = 3

/**============================================== Dialog名称 ==============================================*/
//选择分组dialog
const val SELECT_GROUP_TO_MAIN_DIALOG = "selectGroupToMainDialog"

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

//来源于设置页面
const val RESULT_SOURCE_FROM_RANDOM_SETTING_PAGE = 104