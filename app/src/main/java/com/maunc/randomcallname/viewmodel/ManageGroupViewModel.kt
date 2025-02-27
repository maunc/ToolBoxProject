package com.maunc.randomcallname.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.randomcallname.base.BaseModel
import com.maunc.randomcallname.base.BaseViewModel
import com.maunc.randomcallname.database.randomGroupDao
import com.maunc.randomcallname.database.randomGroupWithNameDao
import com.maunc.randomcallname.database.randomNameDao
import com.maunc.randomcallname.database.table.RandomNameGroup
import com.maunc.randomcallname.database.table.RandomNameWithGroup
import com.maunc.randomcallname.ext.launch
import com.maunc.randomcallname.ext.loge

class ManageGroupViewModel : BaseViewModel<BaseModel>() {

    var groupData = MutableLiveData<List<RandomNameWithGroup>>(mutableListOf())

    var groupDataIsNull = MutableLiveData(true)

    fun queryGroupData() {
        launch({
            randomGroupWithNameDao.queryRandomNameWithGroup()
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
            randomGroupDao.deleteGroupName(groupName)
        }, {
            "deleteGroupData Success".loge()
            deleteAllNameWithGroup(groupName)
        }, {
            "deleteGroupData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }

    private fun deleteAllNameWithGroup(groupName: String) {
        launch({
            randomNameDao.deleteNameWithGroup(groupName)
        }, {
            "deleteAllNameWithGroup Success".loge()
            queryGroupData()
        }, {
            "deleteAllNameWithGroup Error ${it.message}  ${it.stackTrace}".loge()
        })
    }
}