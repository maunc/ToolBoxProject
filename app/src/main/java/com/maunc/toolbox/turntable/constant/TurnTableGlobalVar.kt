package com.maunc.toolbox.turntable.constant

import com.maunc.toolbox.turntable.data.TurnTableEditData
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

/**
 * 过滤标题，生成内容列表 0不过滤 1过滤 返回一个StringList方便使用
 */
fun MutableList<TurnTableEditData>.editDataToStringList(start: Int = 0): MutableList<String> {
    return subList(start, size).map { it.content }.toMutableList()
}

fun TurnTableNameWithGroup.editDataToStringList(): MutableList<String> {
    val list = mutableListOf<String>()
    list.add(turnTableGroupData.groupName)
    turnTableNameDataList.forEach {
        list.add(it.name)
    }
    return list
}

//批量插入数据分批的列表的数量
const val INSERT_TURN_TABLE_EDIT_DATA_CHUNKED_NUM = 5

//编辑数量最少条数(包含标题)
const val MIN_EDIT_DATA_NUMBER = 3

//编辑数量最多条数(包含标题)
const val MAX_EDIT_DATA_NUMBER = 11


//转盘管理列表侧滑菜单位置
const val TURN_TABLE_MANAGE_RECYCLER_SWIPE_EDIT = 0
const val TURN_TABLE_MANAGE_RECYCLER_SWIPE_DELETE = 1

/**============================================== 跳转IntentKey ==============================================*/
const val TURN_TABLE_GROUP_WITH_NAME_EXTRA = "TurnTableWithNameGroup"

/**============================================== 页面来源 ==============================================*/
//来源于转盘设置页面
const val RESULT_SOURCE_FROM_TURN_TABLE_SETTING_PAGE = 201

//来源于创建转盘页面
const val RESULT_SOURCE_FROM_TURN_NEW_TURN_TABLE_PAGE = 202