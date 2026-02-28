package com.maunc.toolbox.turntable.viewmodel

import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.turntable.constant.editDataToStringList
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

class TurnTableBuiltinDataViewModel : BaseViewModel<BaseModel>() {

    fun insertTurnTableEditData(data: TurnTableNameWithGroup, success: () -> Unit = {}) {
        launch({
            turnTableDataDao.insertTurnTableEditData(data.editDataToStringList())
        }, {
            success.invoke()
            "insertTurnTableEditData Success".loge()
        }, {
            "insertTurnTableEditData Error:${it.message},${it.stackTrace}".loge()
        })
    }

    fun selectGroup(groupName: String) {
        launch({
            turnTableDataDao.selectTurnTableGroup(groupName)
        }, {
            "selectGroup Success".loge()
        }, {
            "selectGroup Error:${it.message},${it.stackTrace}".loge()
        })
    }
}