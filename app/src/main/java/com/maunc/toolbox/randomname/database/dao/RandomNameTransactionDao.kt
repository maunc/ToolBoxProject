package com.maunc.toolbox.randomname.database.dao

import androidx.room.Dao
import androidx.room.Transaction
import com.maunc.toolbox.commonbase.database.randomGroupDao
import com.maunc.toolbox.commonbase.database.randomNameDao
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_DESC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_DESC
import com.maunc.toolbox.randomname.database.table.RandomNameData
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup

@Dao
interface RandomNameTransactionDao {

    @Transaction
    fun querySelectGroupData(): RandomNameWithGroup {
        val selectGroup = randomGroupDao.querySelectGroup()
        val randomNameDataList = randomNameDao.queryGroupNameByInsertTime(
            selectGroup.groupName, RANDOM_DB_SORT_BY_INSERT_TIME_ASC
        )
        return RandomNameWithGroup(selectGroup, randomNameDataList)
    }

    @Transaction
    fun queryNameWithGroupByInsertTime(
        querySortType: Int,
    ): MutableList<RandomNameWithGroup> {
        val randomGroupList = randomGroupDao.queryRandomNameGroupByInsertTime(querySortType)
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

    @Transaction
    fun queryNameWithGroupByName(
        querySortType: Int,
    ): MutableList<RandomNameWithGroup> {
        val randomGroupList = randomGroupDao.queryRandomNameGroupByGroupName(querySortType)
        if (randomGroupList.isEmpty()) {
            return mutableListOf()
        }
        val resultList: MutableList<RandomNameWithGroup> = mutableListOf()
        randomGroupList.forEach { groupData ->
            val randomNameData: MutableList<RandomNameData> = mutableListOf()
            randomNameDao.queryGroupNameByName(groupData.groupName, querySortType).let {
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
        return when (querySortType) {
            RANDOM_DB_SORT_BY_INSERT_TIME_ASC, RANDOM_DB_SORT_BY_INSERT_TIME_DESC ->
                randomNameTransactionDao.queryNameWithGroupByInsertTime(querySortType)

            RANDOM_DB_SORT_BY_NAME_ASC, RANDOM_DB_SORT_BY_NAME_DESC ->
                randomNameTransactionDao.queryNameWithGroupByName(querySortType)

            else -> randomNameTransactionDao.queryNameWithGroupByInsertTime(
                RANDOM_DB_SORT_BY_INSERT_TIME_ASC
            )
        }
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
        return when (querySortType) {
            RANDOM_DB_SORT_BY_INSERT_TIME_ASC, RANDOM_DB_SORT_BY_INSERT_TIME_DESC ->
                randomNameDao.queryGroupNameByInsertTime(groupName, querySortType)

            RANDOM_DB_SORT_BY_NAME_ASC, RANDOM_DB_SORT_BY_NAME_DESC ->
                randomNameDao.queryGroupNameByName(groupName, querySortType)

            else -> randomNameDao.queryGroupNameByInsertTime(groupName, querySortType)
        }
    }

    /**
     * 删除所有数据
     */
    @Transaction
    fun deleteAllRandomDataBase() {
        randomGroupDao.deleteAllGroupName()
        randomNameDao.deleteAllRandomName()
    }
}