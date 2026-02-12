package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

class TurnTableDataManagerViewModel : BaseViewModel<BaseModel>() {

    var swipeItemTextSize = 18 //侧滑菜单里面的文本大小
    var turnTableDataIsNull = MutableLiveData<Boolean>()
    var turnTableDataList = MutableLiveData<List<TurnTableNameWithGroup>>()

    var selectTransferData = MutableLiveData(-1)//列表中上次选择的值(视为中转值)
    var selectDataPos = MutableLiveData(-1)//本次选择的值
    var deleteDataPos = MutableLiveData(-1)//本次删除的值

    fun queryTurnTableData() {
        launch({
            turnTableDataDao.queryTurnTableNameWithGroup()
        }, {
            turnTableDataIsNull.value = it.isEmpty()
            turnTableDataList.value = it
            if (it.isNotEmpty()) {
                it.forEachIndexed { index, turnTableNameWithGroup ->
                    if (turnTableNameWithGroup.turnTableGroupData.isSelect) {
                        selectTransferData.value = index //初始中转值
                        return@forEachIndexed
                    }
                }
            }
        }, {
            "queryTurnTableData error $it".loge()
        })
    }

    fun selectTurnTableData(groupName: String, selectPos: Int) {
        launch({
            turnTableDataDao.selectTurnTableGroup(groupName)
        }, {
            selectDataPos.value = selectPos
        }, {
            "selectTurnTableData error $it".loge()
        })
    }

    fun deleteTurnTableData(
        groupName: String,
        deletePos: Int,
    ) {
        launch({
            turnTableDataDao.deleteTurnTableGroup(groupName)
        }, {
            deleteDataPos.value = deletePos
        }, {
            "deleteTurnTableData error $it".loge()
        })
    }
}