package com.maunc.toolbox.commonbase.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maunc.toolbox.commonbase.database.table.RandomNameData

@Dao
interface RandomNameDao {

    @Query("SELECT COUNT(*) FROM random_name WHERE toGroupName=:groupName")
    fun queryGroupToRandomNameSize(groupName: String): Int

    @Query("SELECT * FROM random_name WHERE toGroupName=:groupName AND randomName=:randomName")
    fun queryGroupNameAndRandomName(groupName: String, randomName: String): RandomNameData?

    @Query("SELECT * FROM random_name WHERE toGroupName=:toGroupName")
    fun queryGroupName(toGroupName: String): List<RandomNameData>

    @Query("DELETE FROM random_name WHERE toGroupName=:toGroupName AND randomName=:randomName")
    fun deleteNameWithGroupNameAndRandomName(toGroupName: String, randomName: String)

    @Query("DELETE FROM random_name WHERE toGroupName=:toGroupName ")
    fun deleteNameWithGroup(toGroupName: String)

    @Query("DELETE FROM random_name")
    fun deleteAllRandomName()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRandomName(randomNameData: RandomNameData)
}