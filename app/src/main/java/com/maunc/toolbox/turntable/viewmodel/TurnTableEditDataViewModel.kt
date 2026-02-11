package com.maunc.toolbox.turntable.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.turntable.adapter.TurnTableEditDataAdapter
import com.maunc.toolbox.turntable.data.TurnTableEditData

class TurnTableEditDataViewModel : BaseViewModel<BaseModel>() {

    var showEditErrorTips = MutableLiveData(false)
    var editErrorTips = MutableLiveData(GLOBAL_NONE_STRING)
    var softKeyBroadHeight = MutableLiveData<Int>() //软键盘高度
    var currentSaveDataSize = MutableLiveData(0)//当前保存的数据数量

    /**
     * 全新的数据
     */
    fun initEditAdapterNotData(adapter: TurnTableEditDataAdapter) {
        adapter.addEditTitleItem(GLOBAL_NONE_STRING)
        adapter.addEditNameItem(GLOBAL_NONE_STRING)
        adapter.addEditNameItem(GLOBAL_NONE_STRING)
        currentSaveDataSize.value = 2
    }

    fun insertTurnTableEditData(list: MutableList<TurnTableEditData>) {
        hideErrorTips()
        launch({
            turnTableDataDao.insertTurnTableEditData(list)
        }, {
            "insertTurnTableEditData Success".loge()
        }, {
            "insertTurnTableEditData Error:${it.message},${it.stackTrace}".loge()
        })
    }

    fun handleCurrentSaveSize(size: Int) {
        hideErrorTips()
        currentSaveDataSize.value = size
    }

    fun showErrorTips(errorTips: String) {
        showEditErrorTips.value = true
        editErrorTips.value = errorTips
    }

    fun hideErrorTips() {
        if (!showEditErrorTips.value!!) return
        showEditErrorTips.value = false
    }
}