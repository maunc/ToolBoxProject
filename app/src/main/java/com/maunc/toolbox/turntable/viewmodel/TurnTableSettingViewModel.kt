package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.turntable.data.TurnTableSettingData

class TurnTableSettingViewModel : BaseViewModel<BaseModel>() {

    private var settingItemData =
        MutableLiveData<MutableList<TurnTableSettingData>>(mutableListOf())

    fun initRecyclerData(): MutableList<TurnTableSettingData> {
        settingItemData.value?.mutableListInsert(
            TurnTableSettingData(
                itemType = TurnTableSettingData.TURN_TABLE_MANAGER_DATA_TYPE,
                settingType = obtainString(R.string.turn_table_title_manager_data_tv)
            ),
            TurnTableSettingData(
                itemType = TurnTableSettingData.TURN_TABLE_SELECT_DATA_TYPE,
                settingType = obtainString(R.string.turn_table_title_setting_select_data)
            ),
            TurnTableSettingData(
                itemType = TurnTableSettingData.TURN_TABLE_ENABLE_TOUCH_TYPE,
                settingType = obtainString(R.string.turn_table_title_setting_enable_touch)
            ),
            TurnTableSettingData(
                itemType = TurnTableSettingData.TURN_TABLE_ENABLE_SOUND_EFFECT_TYPE,
                settingType = obtainString(R.string.turn_table_title_setting_enable_sound)
            ),
            TurnTableSettingData(
                itemType = TurnTableSettingData.TURN_TABLE_COLOR_TYPE,
                settingType = obtainString(R.string.turn_table_title_setting_color)
            ),
            TurnTableSettingData(
                itemType = TurnTableSettingData.TURN_TABLE_ANIM_INTERPOLATOR_TYPE,
                settingType = obtainString(R.string.turn_table_title_setting_anim_interpolator)
            ),
            TurnTableSettingData(
                itemType = TurnTableSettingData.TURN_TABLE_DELETE_ALL_DATA_TYPE,
                settingType = obtainString(R.string.turn_table_title_setting_delete_all)
            ),
        )
        return settingItemData.value!!
    }

    fun deleteAllData(deleteSuccess: () -> Unit) {
        launch({
            turnTableDataDao.deleteAllTurnTableData()
        }, {
            deleteSuccess.invoke()
        }, {
            "updateTurnTableEditData Error:${it.message},${it.stackTrace}".loge()
        })
    }
}