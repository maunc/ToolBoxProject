package com.maunc.toolbox.turntable.database.table

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class TurnTableNameWithGroup(
    /*表示是父表*/
    @Embedded
    val turnTableGroupData: TurnTableGroupData,

    @Relation(
        /*parentColumn 是父表对应子表的字段*/
        parentColumn = "groupName",
        /*entityColumn 是子表对应父表的字段*/
        entityColumn = "toGroupName"
    )
    val turnTableNameDataList: MutableList<TurnTableNameData>,
) : Serializable
