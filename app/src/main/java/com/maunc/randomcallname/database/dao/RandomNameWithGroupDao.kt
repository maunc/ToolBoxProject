package com.maunc.randomcallname.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.maunc.randomcallname.database.table.RandomNameWithGroup

@Dao
interface RandomNameWithGroupDao {

    @Transaction
    @Query("SELECT * FROM random_name_group") /*from 父表*/
    fun queryRandomNameWithGroup(): MutableList<RandomNameWithGroup>
}