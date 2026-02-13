package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.turnTableAnimSoundEffect
import com.maunc.toolbox.commonbase.utils.turnTableEnableTouch
import com.maunc.toolbox.turntable.database.table.TurnTableNameData

class TurnTableMainViewModel : BaseViewModel<BaseModel>() {

    var turnTableIsEnableTouch = MutableLiveData(false) // 转盘是否可以触摸
    var turnTableIsEnableSoundEffect = MutableLiveData(false) // 是否启用转盘音效

    //当前选中的数据
    var currentSelectTitle = MutableLiveData(GLOBAL_NONE_STRING)
    var currentSelectData = MutableLiveData<MutableList<String>>()

    // 当前转到的数据
    var turnTableAnimSelectContent = MutableLiveData(GLOBAL_NONE_STRING)

    // 上一次转到的下标
    var turnTableAnimLastSelectIndex = 0

    fun initViewModelConfig() {
        initSettingConfig()
    }

    fun initSettingConfig(
        enableTouch: Boolean = obtainMMKV.getBoolean(turnTableEnableTouch),
        enableSoundEffect: Boolean = obtainMMKV.getBoolean(turnTableAnimSoundEffect),
    ) {
        turnTableIsEnableTouch.value = enableTouch
        turnTableIsEnableSoundEffect.value = enableSoundEffect
    }

    fun initSelectTurnTableData() {
        launch({
            turnTableDataDao.queryCurrentSelectGroup()
        }, {
            if (it != null) {
                currentSelectTitle.value = it.turnTableGroupData.groupName
                currentSelectData.value = it.turnTableNameDataList.dataBaseDataToStringList()
                turnTableAnimSelectContent.value = it.turnTableGroupData.groupName
            } else {
                currentSelectTitle.value = GLOBAL_NONE_STRING
                currentSelectData.value = mutableListOf(
                    "测试数据1", "测试数据2", "测试数据3",
                    "测试数据4", "测试数据5", "测试数据6",
                )
                turnTableAnimSelectContent.value = GLOBAL_NONE_STRING
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