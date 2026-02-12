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

    @Query("SELECT * FROM turn_table_group WHERE groupName=:groupName")
    fun queryTurnTableGroup(groupName: String): TurnTableGroupData?

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
     * 删除分组标题(关联子表配置了自动删除)
     */
    @Transaction
    @Query("DELETE FROM turn_table_group WHERE groupName=:groupName")
    fun deleteTurnTableGroup(groupName: String)

    /**
     * 删除某一个分组下的选项
     */
    @Query("DELETE FROM turn_table_name WHERE toGroupName=:toGroupName AND name=:name")
    fun deleteTurnTableName(toGroupName: String, name: String)

    @Query("UPDATE turn_table_group SET isSelect=:isSelect")
    fun updateCancelAllSelectStatus(isSelect: Boolean = false)

    @Query("UPDATE turn_table_group SET isSelect=:isSelect WHERE groupName=:groupName")
    fun updateSelectStatus(groupName: String, isSelect: Boolean = true): Int

    /**
     * 修改组名
     */
    @Query("UPDATE turn_table_group SET groupName=:newGroupName WHERE groupName=:oldGroupName")
    fun updateGroupName(oldGroupName: String, newGroupName: String)

    /**
     * 修改小组下的名称
     */
    @Query("UPDATE turn_table_name SET name=:newName WHERE name=:oldName AND toGroupName=:toGroupName")
    fun updateName(oldName: String, newName: String, toGroupName: String)

    /**
     * 删除对应分组的名称
     */
    @Query("DELETE FROM turn_table_name WHERE toGroupName=:toGroupName AND name=:name")
    fun deleteNameByGroupName(toGroupName: String, name: String)

    /**
     * 选中该分组
     */
    @Transaction
    fun selectTurnTableGroup(groupName: String): Boolean {
        val randomNameGroup = queryTurnTableGroup(groupName)
        if (randomNameGroup?.isSelect == true) {
            return false
        }
        updateCancelAllSelectStatus()
        val updateSelectStatus = updateSelectStatus(groupName)
        return updateSelectStatus == 1
    }

    /**
     * 批量修改数据
     */
    @Transaction
    fun updateTurnTableEditData(
        oldTitle: String,
        newTitle: String,
        oldList: MutableList<String>,
        newList: MutableList<String>,
    ) {
        // 更新标题
        if (oldTitle != newTitle) {
            updateGroupName(oldTitle, newTitle)
        }
        // 遍历旧的数据如果在新集合中找不到就删除
        oldList.filter { it !in newList }.takeIf { it.isNotEmpty() }?.forEach { name ->
            deleteNameByGroupName(newTitle, name)
        }
        // 遍历新的数据如果在旧集合中找不到就添加
        newList.filter { it !in oldList }.takeIf { it.isNotEmpty() }?.forEach { name ->
            insertTurnTableName(
                TurnTableNameData(toGroupName = newTitle, name = name)
            )
        }
    }

    /**
     * 插入数据一份完整转盘数据
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