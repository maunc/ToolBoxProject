package com.maunc.randomcallname.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.randomcallname.base.BaseModel
import com.maunc.randomcallname.base.BaseViewModel
import com.maunc.randomcallname.database.randomNameDao
import com.maunc.randomcallname.database.table.RandomNameData
import com.maunc.randomcallname.ext.launch
import com.maunc.randomcallname.ext.loge

class ManageGroupWithNameViewModel : BaseViewModel<BaseModel>() {

    var groupData = MutableLiveData<List<RandomNameData>>(mutableListOf())

    var groupDataIsNull = MutableLiveData(true)

    //是否更改过数据库
    var whetherDataHasChange = MutableLiveData(false)

    fun queryGroupWithNameData(groupName: String) {
        launch({
            randomNameDao.queryGroupName(groupName)
        }, {
            "queryGroupWithNameData Success data->${it.isEmpty()}".loge()
            groupData.value = it
            groupDataIsNull.value = it.isEmpty()
        }, {
            "queryGroupWithNameData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }

    fun deleteGroupWithNameData(groupName: String, randomName: String) {
        launch({
            randomNameDao.deleteNameWithGroupNameAndRandomName(groupName, randomName)
        }, {
            "deleteGroupWithNameData Success".loge()
            whetherDataHasChange.value = true
            queryGroupWithNameData(groupName)
        }, {
            "deleteGroupWithNameData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }
}