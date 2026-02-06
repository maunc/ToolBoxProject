package com.maunc.toolbox.turntable.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

@Dao
interface TurnTableDataDao {

    /**
     * 查询所有表一对多
     */
    @Transaction
    @Query("SELECT * FROM turn_table_group")
    fun queryTurnTableNameWithGroup(): List<TurnTableNameWithGroup>

    /**
     * 根据分组名称查询表一对多
     * @param groupName 分组名称
     */
    @Transaction
    @Query("SELECT * FROM turn_table_group WHERE groupName=:groupName")
    fun queryTurnTableNameWithGroupByGroupName(groupName: String): TurnTableNameWithGroup
}