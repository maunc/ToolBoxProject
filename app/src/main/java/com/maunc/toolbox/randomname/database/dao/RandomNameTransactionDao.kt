package com.maunc.toolbox.randomname.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.maunc.toolbox.commonbase.constant.ARRAY_INDEX_ZERO
import com.maunc.toolbox.commonbase.database.randomGroupDao
import com.maunc.toolbox.commonbase.database.randomNameDao
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.randomname.database.table.RandomNameData
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup

@Dao
interface RandomNameTransactionDao {

    @Transaction
    @Query("SELECT * FROM random_name_group") /*from 父表*/
    fun queryNameWithGroup(
        querySortType: Int,
    ): MutableList<RandomNameWithGroup> {
        val randomGroupList = randomGroupDao.queryRandomNameGroup(querySortType)
        if (randomGroupList.isEmpty()) {
            return mutableListOf()
        }
        val resultList: MutableList<RandomNameWithGroup> = mutableListOf()
        randomGroupList.forEach { groupData ->
            val randomNameData: MutableList<RandomNameData> = mutableListOf()
            randomNameDao.queryGroupNameByInsertTime(groupData.groupName, querySortType).let {
                it.forEach { data ->
                    randomNameData.add(data)
                }
            }
            resultList.add(RandomNameWithGroup(groupData, randomNameData))
        }
        return resultList
    }

    /**
     * 删掉分组并删掉分组对应的所有名称，然后再组合查一遍
     */
    @Transaction
    fun deleteGroupAndQueryRandomAllData(
        groupName: String,
        querySortType: Int,
    ): MutableList<RandomNameWithGroup> {
        randomGroupDao.deleteGroupName(groupName)
        randomNameDao.deleteNameWithGroup(groupName)
        return randomNameTransactionDao.queryNameWithGroup(querySortType)
    }

    /**
     * 删掉名称并查一遍 分组下的所有名称
     */
    @Transaction
    fun deleteNameAndQueryNameData(
        groupName: String,
        randomName: String,
        querySortType: Int,
    ): MutableList<RandomNameData> {
        randomNameDao.deleteNameWithGroupNameAndRandomName(groupName, randomName)
        return randomNameDao.queryGroupNameByInsertTime(groupName, querySortType)
    }
}