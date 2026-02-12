package com.maunc.toolbox.turntable.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 名字子表
 */
@Entity(
    tableName = "turn_table_name",
    foreignKeys = [
        ForeignKey(
            entity = TurnTableGroupData::class,
            parentColumns = ["groupName"],
            childColumns = ["toGroupName"],
            onDelete = ForeignKey.CASCADE, //父表删除一个段，对应子表的所有字段都删除
            onUpdate = ForeignKey.CASCADE //父表修改一个段，对应子表的所有字段都修改
        )
    ],
    indices = [Index(value = ["toGroupName"])]// 关联字段加索引
)
data class TurnTableNameData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    var id: Int = 0,

    @ColumnInfo("toGroupName")
    var toGroupName: String,

    @ColumnInfo("name")
    var name: String,
)