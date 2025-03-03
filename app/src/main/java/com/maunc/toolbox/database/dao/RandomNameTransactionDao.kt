package com.maunc.toolbox.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.maunc.toolbox.database.randomGroupDao
import com.maunc.toolbox.database.randomNameTransactionDao
import com.maunc.toolbox.database.randomNameDao
import com.maunc.toolbox.database.table.RandomNameData
import com.maunc.toolbox.database.table.RandomNameWithGroup

@Dao
interface RandomNameTransactionDao {

    @Transaction
    @Query("SELECT * FROM random_name_group") /*from 父表*/
    fun queryNameWithGroup(): MutableList<RandomNameWithGroup>

    /**
     * 删掉分组并删掉分组对应的所有名称，然后再组合查一遍
     */
    @Transaction
    fun deleteGroupAndQueryRandomAllData(groupName: String): List<RandomNameWithGroup> {
        randomGroupDao.deleteGroupName(groupName)
        randomNameDao.deleteNameWithGroup(groupName)
        return randomNameTransactionDao.queryNameWithGroup()
    }

    /**
     * 删掉名称并查一遍 分组下的所有名称
     */
    @Transaction
    fun deleteNameAndQueryNameData(groupName: String, randomName: String) :List<RandomNameData>{
        randomNameDao.deleteNameWithGroupNameAndRandomName(groupName, randomName)
        return randomNameDao.queryGroupName(groupName)
    }
}