package com.maunc.toolbox.randomname.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.randomname.data.RandomSettingData

class RandomSettingViewModel : BaseRandomNameViewModel<BaseModel>() {

    private var settingItemData = MutableLiveData<MutableList<RandomSettingData>>(mutableListOf())

    var handler: Handler? = Handler(Looper.getMainLooper())

    fun initRecyclerData(): MutableList<RandomSettingData> {
        settingItemData.value?.mutableListInsert(
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_SELECT_DATA_TYPE,
                settingType = obtainString(R.string.random_setting_select_data_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_MANAGER_DATA_TYPE,
                settingType = obtainString(R.string.random_setting_manager_data_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_BUTTON_VIBRATOR_TYPE,
                settingType = obtainString(R.string.random_setting_vibrator_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_SPEED_TYPE,
                settingType = obtainString(R.string.random_setting_sleep_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_SELECT_LIST_TYPE,
                settingType = obtainString(R.string.random_setting_not_select_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_RESULT_TEXT_BOLD_TYPE,
                settingType = obtainString(R.string.random_setting_result_text_bold_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_NAME_TYPE_TYPE,
                settingType = obtainString(R.string.random_setting_random_type_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_MANAGE_SORT_TYPE,
                settingType = obtainString(R.string.random_setting_random_db_sort_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_DELETE_ALL_DATA_TYPE,
                settingType = obtainString(R.string.random_setting_random_delete_all_data_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_BUTTON_EGGS_TYPE,
                settingType = obtainString(R.string.random_setting_eggs_text)
            )
        )
        return settingItemData.value!!
    }

    fun deleteAllData(
        deleteResultCallback: (Boolean) -> Unit = {},
    ) {
        launch({
            randomNameTransactionDao.deleteAllRandomDataBase()
        }, {
            deleteResultCallback(true)
            "delete all data success".loge()
        }, {
            deleteResultCallback(false)
            "delete all data error:${it.message}  ${it.stackTrace}".loge()
        })
    }

    override fun onCleared() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        super.onCleared()
    }
}