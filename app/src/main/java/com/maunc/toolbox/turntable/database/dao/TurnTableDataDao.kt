package com.maunc.toolbox.turntable.database.dao

import androidx.room.Query
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

interface TurnTableDataDao {

    /**
     * 查询所有表一对多
     */
    @Query("SELECT * FROM turn_table_group")
    fun queryTurnTableNameWithGroup(): List<TurnTableNameWithGroup>

    /**
     * 根据分组名称查询表一对多
     * @param groupName 分组名称
     */
    @Query("SELECT * FROM turn_table_group WHERE groupName")
    fun queryTurnTableNameWithGroupByGroupName(groupName: String): TurnTableNameWithGroup
}