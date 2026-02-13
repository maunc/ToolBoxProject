package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.turnTableEnableTouch
import com.maunc.toolbox.turntable.database.table.TurnTableNameData

class TurnTableMainViewModel : BaseViewModel<BaseModel>() {

    var turnTableIsEnableTouch = MutableLiveData(false) // 转盘是否可以触摸

    //当前选中的数据
    var currentSelectTitle = MutableLiveData(GLOBAL_NONE_STRING)
    var currentSelectData = MutableLiveData<MutableList<String>>()

    fun initViewModelConfig() {
        initSettingConfig()
    }

    fun initSettingConfig(
        enableTouch: Boolean = obtainMMKV.getBoolean(turnTableEnableTouch),
    ) {
        turnTableIsEnableTouch.value = enableTouch
    }

    fun initSelectTurnTableData() {
        launch({
            turnTableDataDao.queryCurrentSelectGroup()
        }, {
            it?.let {
                currentSelectTitle.value = it.turnTableGroupData.groupName
                currentSelectData.value = it.turnTableNameDataList.dataBaseDataToStringList()
            }
            "initSelectTurnTableData success $it".loge()
        }, {
            "initSelectTurnTableData error $it".loge()
        })
    }

    /**
     * 生成内容列表
     */
    private fun MutableList<TurnTableNameData>.dataBaseDataToStringList(): MutableList<String> {
        return map { it.name }.toMutableList()
    }
}