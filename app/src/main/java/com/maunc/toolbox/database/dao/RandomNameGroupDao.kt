package com.maunc.toolbox.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maunc.toolbox.database.table.RandomNameGroup

@Dao
interface RandomNameGroupDao {

    @Query("SELECT * FROM random_name_group")
    fun queryRandomNameGroup(): List<RandomNameGroup>

    @Query("SELECT * FROM random_name_group WHERE groupName=:groupName")
    fun queryRandomNameGroup(groupName: String): RandomNameGroup?

    @Query("SELECT COUNT(*) FROM random_name_group")
    fun queryRandomNameGroupSize(): Int

    @Query("DELETE FROM random_name_group WHERE groupName=:groupName")
    fun deleteGroupName(groupName: String)

    @Query("DELETE FROM random_name_group")
    fun deleteAllGroupName()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRandomNameGroup(randomNameGroup: RandomNameGroup)
}