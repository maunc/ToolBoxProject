package com.maunc.toolbox.randomname.viewmodel

import com.maunc.base.ext.loge
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.base.ui.launch
import com.maunc.toolbox.commonbase.database.randomGroupDao
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup

class RandomBuiltinDataViewModel : BaseViewModel<BaseModel>() {

    fun insertRandomData(
        randomData: RandomNameWithGroup,
        success: () -> Unit,
    ) {
        launch({
            randomNameTransactionDao.insertGroupWithName(randomData)
        }, {
            success.invoke()
            "insertTurnTableEditData Success".loge()
        }, {
            "insertTurnTableEditData Error:${it.message},${it.stackTrace}".loge()
        })
    }

    fun selectGroup(groupName: String) {
        launch({
            randomGroupDao.selectGroup(groupName)
        }, {
            "selectGroup Success".loge()
        }, {
            "selectGroup Error:${it.message},${it.stackTrace}".loge()
        })
    }
}