package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

class TurnTableDataManagerViewModel : BaseViewModel<BaseModel>() {
    var turnTableDataList = MutableLiveData<List<TurnTableNameWithGroup>>()

    fun queryTurnTableData() {
        launch({
            turnTableDataDao.queryTurnTableNameWithGroup()
        }, {
            turnTableDataList.value = it
        }, {
            "queryTurnTableData error $it".loge()
        })
    }
}