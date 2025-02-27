package com.maunc.randomcallname.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.randomcallname.base.BaseModel
import com.maunc.randomcallname.base.BaseViewModel
import com.maunc.randomcallname.database.randomNameTransactionDao
import com.maunc.randomcallname.database.table.RandomNameWithGroup
import com.maunc.randomcallname.ext.launch
import com.maunc.randomcallname.ext.loge

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