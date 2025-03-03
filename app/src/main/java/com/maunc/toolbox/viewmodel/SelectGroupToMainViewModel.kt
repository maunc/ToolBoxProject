package com.maunc.toolbox.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.base.BaseModel
import com.maunc.toolbox.base.BaseViewModel
import com.maunc.toolbox.constant.GROUP_REMOVE_THRESHOLD
import com.maunc.toolbox.database.randomNameTransactionDao
import com.maunc.toolbox.database.table.RandomNameWithGroup
import com.maunc.toolbox.ext.getString
import com.maunc.toolbox.ext.launch
import com.maunc.toolbox.ext.loge

class SelectGroupToMainViewModel : BaseViewModel<BaseModel>() {

    var groupData = MutableLiveData<MutableList<RandomNameWithGroup>>(mutableListOf())

    var groupDataIsNull = MutableLiveData<Boolean>()

    var showTipsBool = MutableLiveData<Boolean>()
    var tipsStrVar = MutableLiveData<String>()

    fun queryGroupData() {
        launch({
            randomNameTransactionDao.queryNameWithGroup()
        }, {
            "queryGroupData Success data->${it.isEmpty()}".loge()
            handleGroupData(it)
        }, {
            "queryGroupData Error ${it.message}  ${it.stackTrace}".loge()
        })
    }

    private fun handleGroupData(groupList: MutableList<RandomNameWithGroup>) {
        val iterator = groupList.listIterator()
        while (iterator.hasNext()) {
            val group = iterator.next()
            if (group.randomNameDataList.isEmpty()) {
                iterator.remove()
            }
            if (group.randomNameDataList.size == GROUP_REMOVE_THRESHOLD) {
                iterator.remove()
            }
        }
        groupData.value = groupList
        groupDataIsNull.value = groupList.isEmpty()
        handleTips(groupList.isEmpty())
    }

    private fun handleTips(groupIsEmpty: Boolean) {
        if (groupIsEmpty) {
            showTipsBool.value = false
        } else {
            showTipsBool.value = true
            tipsStrVar.value = getString(R.string.select_group_to_main_tips_text)
        }
    }
}