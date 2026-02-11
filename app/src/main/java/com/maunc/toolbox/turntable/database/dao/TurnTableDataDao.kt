package com.maunc.toolbox.turntable.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.maunc.toolbox.turntable.constant.INSERT_TURN_TABLE_EDIT_DATA_CHUNKED_NUM
import com.maunc.toolbox.turntable.constant.MIN_EDIT_DATA_NUMBER
import com.maunc.toolbox.turntable.data.TurnTableEditData
import com.maunc.toolbox.turntable.database.table.TurnTableGroupData
import com.maunc.toolbox.turntable.database.table.TurnTableNameData
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

@Dao
interface TurnTableDataDao {

    /**
     * 插入分组标题
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTurnTableGroup(turnTableGroup: TurnTableGroupData)

    /**
     * 插入分组选项
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTurnTableName(turnTableName: TurnTableNameData)

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

    /**
     * 批量插入数据
     */
    @Transaction
    fun insertTurnTableEditData(editDataList: MutableList<TurnTableEditData>) {
        if (editDataList.isEmpty()) return
        if (editDataList.size < MIN_EDIT_DATA_NUMBER) return
        val turnTableTitle = editDataList[0].content
        // 第一个是标题
        insertTurnTableGroup(TurnTableGroupData(turnTableTitle))
        editDataList.drop(1).chunked(INSERT_TURN_TABLE_EDIT_DATA_CHUNKED_NUM).forEach {
            it.forEach { name ->
                insertTurnTableName(
                    TurnTableNameData(
                        toGroupName = turnTableTitle, name = name.content
                    )
                )
            }
        }
    }
}