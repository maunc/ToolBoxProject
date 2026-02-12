package com.maunc.toolbox.turntable.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 组名父表
 */
@Entity(tableName = "turn_table_group")
data class TurnTableGroupData(
    @PrimaryKey
    @ColumnInfo("groupName")
    var groupName: String,

    @ColumnInfo(name = "isSelect")
    var isSelect: Boolean = false,

    @ColumnInfo(name = "isExpand")
    var isExpand: Boolean = false,

    @ColumnInfo(name = "createTime")
    var createTime: Long = System.currentTimeMillis(),
)