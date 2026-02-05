package com.maunc.toolbox.turntable.database.table

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 组名父表
 */
@Entity(tableName = "turn_table_group")
data class TurnTableGroupData(
    @PrimaryKey
    var groupName: String,
)