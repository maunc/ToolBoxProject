package com.maunc.toolbox.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.base.BaseModel
import com.maunc.toolbox.base.BaseViewModel
import com.maunc.toolbox.database.randomNameTransactionDao
import com.maunc.toolbox.database.table.RandomNameWithGroup
import com.maunc.toolbox.ext.launch
import com.maunc.toolbox.ext.loge

class ManageGroupViewModel : BaseViewModel<BaseModel>() {

    var groupData = MutableLiveData<List<RandomNameWithGroup>>(mutableListOf())

    var groupDataIsNull = MutableLiveData(true)

    fun queryGroupData() {
        launch({
            randomNameTransactionDao.queryNameWithGroup()
        }, {
            "queryGroupData Success data->${it.isEmpty()}".loge()
            groupData.value = it
            groupDataIsNull.value = it.isEmpty()
        }, {
            "queryGroupData Error ${it.message}  ${it.stackTrace}".loge()
            groupData.value = mutableListOf()
        })
    }

    fun deleteGroupData(groupName: String) {
        launch({
            randomNameTransactionDao.deleteGroupAndQueryRandomAllData(groupName)
        }, {
            "deleteGroupData Success".loge()
            groupData.value = it
            groupDataIsNull.value = it.isEmpty()
        }, {
            "deleteGroupData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }
}