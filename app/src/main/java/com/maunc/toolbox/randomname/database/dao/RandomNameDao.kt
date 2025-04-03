package com.maunc.toolbox.randomname.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maunc.toolbox.randomname.database.table.RandomNameData

@Dao
interface RandomNameDao {

    @Query("SELECT COUNT(*) FROM random_name WHERE toGroupName=:groupName")
    fun queryGroupToRandomNameSize(
        groupName: String,
    ): Int

    @Query("SELECT * FROM random_name WHERE toGroupName=:groupName AND randomName=:randomName")
    fun queryGroupNameAndRandomName(
        groupName: String,
        randomName: String,
    ): RandomNameData?

    @Query(
        "SELECT * FROM random_name WHERE toGroupName=:toGroupName order by " +
                "CASE WHEN :querySortType=0 THEN insertNameTime END ASC," +
                "CASE WHEN :querySortType=1 THEN insertNameTime END DESC"
    )
    fun queryGroupNameByInsertTime(
        toGroupName: String,
        querySortType: Int,
    ): MutableList<RandomNameData>

    @Query(
        "SELECT * FROM random_name WHERE toGroupName=:toGroupName order by " +
                "CASE WHEN :querySortType=2 THEN randomName COLLATE LOCALIZED END ASC," +
                "CASE WHEN :querySortType=3 THEN randomName COLLATE LOCALIZED END DESC"
    )
    fun queryGroupNameByName(
        toGroupName: String,
        querySortType: Int,
    ): MutableList<RandomNameData>

    @Query("DELETE FROM random_name WHERE toGroupName=:toGroupName AND randomName=:randomName")
    fun deleteNameWithGroupNameAndRandomName(toGroupName: String, randomName: String)

    @Query("DELETE FROM random_name WHERE toGroupName=:toGroupName ")
    fun deleteNameWithGroup(toGroupName: String)

    @Query("DELETE FROM random_name")
    fun deleteAllRandomName()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRandomName(randomNameData: RandomNameData)
}