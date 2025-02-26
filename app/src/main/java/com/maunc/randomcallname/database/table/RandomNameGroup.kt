package com.maunc.randomcallname.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "random_name_group", primaryKeys = ["groupName"])
data class RandomNameGroup(
    @ColumnInfo(name = "groupName")
    var groupName: String,
    @ColumnInfo(name = "isExpand")
    var isExpand: Boolean = false,
) : Serializable
