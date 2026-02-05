package com.maunc.toolbox.turntable.database.dao

import androidx.room.Query
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

interface TurnTableDataDao {

    @Query("SELECT * FROM turn_table_group")
    fun queryTurnTableNameWithGroup(): List<TurnTableNameWithGroup>

    @Query("SELECT * FROM turn_table_group WHERE groupName")
    fun queryTurnTableNameWithGroupByGroupName(groupName: String): TurnTableNameWithGroup
}