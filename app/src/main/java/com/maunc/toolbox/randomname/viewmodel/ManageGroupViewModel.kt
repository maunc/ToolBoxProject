package com.maunc.toolbox.randomname.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomListSortType
import com.maunc.toolbox.randomname.constant.RANDOM_DB_SORT_BY_INSERT_TIME_DESC
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup

class ManageGroupViewModel : BaseRandomNameViewModel<BaseModel>() {

    private var dbSortType = MutableLiveData(obtainMMKV.getInt(randomListSortType))

    var groupData = MutableLiveData<List<RandomNameWithGroup>>(mutableListOf())

    var groupDataIsNull = MutableLiveData(true)

    fun queryGroupData() {
        launch({
            randomNameTransactionDao.queryNameWithGroup(dbSortType.value!!)
        }, {
            groupData.value = it
            groupDataIsNull.value = it.isEmpty()
        }, {
            "queryGroupData Error ${it.message}  ${it.stackTrace}".loge()
            groupData.value = mutableListOf()
        })
    }

    fun deleteGroupData(groupName: String) {
        launch({
            randomNameTransactionDao.deleteGroupAndQueryRandomAllData(groupName, dbSortType.value!!)
        }, {
            groupData.value = it
            groupDataIsNull.value = it.isEmpty()
        }, {
            "deleteGroupData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }
}