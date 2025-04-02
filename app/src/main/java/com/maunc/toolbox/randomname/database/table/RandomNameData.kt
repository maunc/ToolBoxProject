package com.maunc.toolbox.randomname.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "random_name", primaryKeys = ["toGroupName", "randomName"])
data class RandomNameData(
    @ColumnInfo(name = "toGroupName")
    var toGroupName: String,
    @ColumnInfo(name = "randomName")
    var randomName: String,
    @ColumnInfo(name = "insertNameTime")
    var insertNameTime: Long = System.currentTimeMillis()
) : Serializable