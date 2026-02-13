package com.maunc.toolbox.turntable.constant

import android.graphics.Color
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.turntable.data.TurnTableConfigColorData
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

/**============================================== 预制数据 ==============================================*/
val configColorList = mutableListOf<TurnTableConfigColorData>().apply {
    mutableListInsert(
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#00E616"),
                Color.parseColor("#E70000"),
                Color.parseColor("#0083EB"),
            )
        }),
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#008AC8"),
                Color.parseColor("#CC008AC8"),//80
                Color.parseColor("#99008AC8"),//60
                Color.parseColor("#66008AC8"),//40
                Color.parseColor("#33008AC8"),//20
            )
        }),
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#3F65E8"),
                Color.parseColor("#803F65E8")
            )
        }),
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#76C741"),
                Color.parseColor("#8076C741")
            )
        }),
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#FC3131"),
                Color.parseColor("#80FC3131")
            )
        }),
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#FA7F21"),
                Color.parseColor("#80FA7F21")
            )
        }),
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#A23B00"),
                Color.parseColor("#80A23B00")
            )
        }),
        TurnTableConfigColorData(mutableListOf<Int>().apply {
            mutableListInsert(
                Color.parseColor("#805D99"),
                Color.parseColor("#80805D99")
            )
        }),
    )
}