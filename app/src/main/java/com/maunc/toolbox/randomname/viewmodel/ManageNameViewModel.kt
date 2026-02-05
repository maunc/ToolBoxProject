package com.maunc.toolbox.randomname.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.database.randomNameDao
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomListSortType
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_DESC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_ASC
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_NAME_DESC
import com.maunc.toolbox.randomname.database.table.RandomNameData

class ManageNameViewModel : BaseRandomNameViewModel<BaseModel>() {

    private var dbSortType = MutableLiveData(obtainMMKV.getInt(randomListSortType))

    var groupData = MutableLiveData<List<RandomNameData>>(mutableListOf())

    var groupDataIsNull = MutableLiveData(true)

    //是否更改过数据库
    var whetherDataHasChange = MutableLiveData(false)

    fun queryGroupWithNameData(toGroupName: String) {
        launch({
            when (dbSortType.value!!) {
                RANDOM_DB_SORT_BY_INSERT_TIME_ASC, RANDOM_DB_SORT_BY_INSERT_TIME_DESC ->
                    randomNameDao.queryGroupNameByInsertTime(toGroupName, dbSortType.value!!)

                RANDOM_DB_SORT_BY_NAME_ASC, RANDOM_DB_SORT_BY_NAME_DESC ->
                    randomNameDao.queryGroupNameByName(toGroupName, dbSortType.value!!)

                else -> randomNameDao.queryGroupNameByInsertTime(
                    toGroupName, RANDOM_DB_SORT_BY_INSERT_TIME_ASC
                )
            }
        }, {
            handleGroupData(it, it.isEmpty())
        }, {
            "queryGroupWithNameData Error ${it.message}  ${it.stackTrace}".loge()
            groupData.value = mutableListOf()
        })
    }

    fun deleteGroupWithNameData(
        toGroupName: String,
        randomName: String,
    ) {
        launch({
            randomNameTransactionDao.deleteNameAndQueryNameData(
                toGroupName, randomName, dbSortType.value!!
            )
        }, {
            whetherDataHasChange.value = true
            handleGroupData(it, it.isEmpty())
        }, {
            "deleteGroupWithNameData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }

    private fun handleGroupData(
        randomNameDataList: List<RandomNameData>,
        isEmpty: Boolean,
    ) {
        groupData.value = randomNameDataList
        groupDataIsNull.value = isEmpty
    }
}