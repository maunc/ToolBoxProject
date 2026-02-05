package com.maunc.toolbox.turntable.database.table

import androidx.room.Entity
import androidx.room.ForeignKey
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
            onDelete = ForeignKey.CASCADE //父表删除一个段，对应子表的所有字段都删除
        )
    ]
)
data class TurnTableNameData(
    var toGroupName: Int,
    var name: String,
)