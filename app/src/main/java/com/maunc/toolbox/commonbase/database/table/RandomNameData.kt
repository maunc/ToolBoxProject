package com.maunc.toolbox.commonbase.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "random_name", primaryKeys = ["toGroupName", "randomName"])
data class RandomNameData(
    @ColumnInfo(name = "toGroupName")
    var toGroupName: String,
    @ColumnInfo(name = "randomName")
    var randomName: String,
) : Serializable