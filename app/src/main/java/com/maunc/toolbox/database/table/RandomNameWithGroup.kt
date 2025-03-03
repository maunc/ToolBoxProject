package com.maunc.toolbox.database.table

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class RandomNameWithGroup(
    /*表示是父表*/
    @Embedded
    val randomNameGroup: RandomNameGroup,
    @Relation(
        /*parentColumn 是父表对应子表的字段*/
        parentColumn = "groupName",
        /*entityColumn 是子表对应父表的字段*/
        entityColumn = "toGroupName"
    )
    val randomNameDataList: List<RandomNameData>,
) : Serializable
