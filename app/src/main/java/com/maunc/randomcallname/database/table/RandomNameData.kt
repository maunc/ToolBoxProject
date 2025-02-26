package com.maunc.randomcallname.database.table

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import java.io.Serializable

@Entity(tableName = "random_name", primaryKeys = ["toGroupName", "randomName"])
data class RandomNameData(
    @ColumnInfo(name = "toGroupName")
    var toGroupName: String,
    @ColumnInfo(name = "randomName")
    var randomName: String,
) : Serializable