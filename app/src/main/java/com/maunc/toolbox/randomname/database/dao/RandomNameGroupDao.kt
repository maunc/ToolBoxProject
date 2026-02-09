package com.maunc.toolbox.randomname.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.maunc.toolbox.randomname.database.table.RandomNameGroup

@Dao
interface RandomNameGroupDao {

    @Query(
        "SELECT * FROM random_name_group order by " +
                "CASE WHEN :querySortType=0 THEN insertGroupTime END ASC," +
                "CASE WHEN :querySortType=1 THEN insertGroupTime END DESC"
    )
    fun queryRandomNameGroupByInsertTime(
        querySortType: Int,
    ): MutableList<RandomNameGroup>

    @Query(
        "SELECT * FROM random_name_group order by " +
                "CASE WHEN :querySortType=2 THEN groupName COLLATE LOCALIZED END ASC," +
                "CASE WHEN :querySortType=3 THEN groupName COLLATE LOCALIZED END DESC"
    )
    fun queryRandomNameGroupByGroupName(
        querySortType: Int,
    ): MutableList<RandomNameGroup>

    @Query("SELECT * FROM random_name_group WHERE groupName=:groupName")
    fun queryRandomNameGroup(
        groupName: String,
    ): RandomNameGroup?

    @Query("SELECT COUNT(*) FROM random_name_group")
    fun queryRandomNameGroupSize(): Int

    @Query("DELETE FROM random_name_group WHERE groupName=:groupName")
    fun deleteGroupName(groupName: String)

    @Query("DELETE FROM random_name_group")
    fun deleteAllGroupName()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRandomNameGroup(randomNameGroup: RandomNameGroup)

    @Query("UPDATE random_name_group SET isSelect = :isSelect")
    fun updateCancelAllSelectStatus(isSelect: Boolean = false)

    @Query("UPDATE random_name_group SET isSelect = :isSelect WHERE groupName=:groupName")
    fun updateSelectStatus(groupName: String, isSelect: Boolean = true): Int

    @Transaction
    fun selectGroup(groupName: String): Boolean {
        updateCancelAllSelectStatus()
        val updateSelectStatus = updateSelectStatus(groupName)
        return updateSelectStatus == 1
    }
}