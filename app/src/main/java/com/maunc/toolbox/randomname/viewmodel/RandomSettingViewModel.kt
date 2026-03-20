package com.maunc.toolbox.randomname.viewmodel

import com.maunc.base.ext.loge
import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.base.ui.launch
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.database.randomNameTransactionDao
import com.maunc.toolbox.randomname.data.RandomSettingData

class RandomSettingViewModel : BaseViewModel<BaseModel>() {

    fun initRecyclerData() = mutableListOf<RandomSettingData>().mutableListInsert(
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_SELECT_DATA_TYPE,
            settingType = obtainString(R.string.random_setting_select_data_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_MANAGER_DATA_TYPE,
            settingType = obtainString(R.string.random_setting_manager_data_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_BUILTIN_DATA_TYPE,
            settingType = obtainString(R.string.random_setting_builtin_data_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_SPEED_TYPE,
            settingType = obtainString(R.string.random_setting_sleep_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_NAME_TYPE_TYPE,
            settingType = obtainString(R.string.random_setting_random_type_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_SELECT_LIST_TYPE,
            settingType = obtainString(R.string.random_setting_not_select_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_ENUM_TYPE,
            settingType = obtainString(R.string.random_setting_enum_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_ALLOW_REPEAT_TYPE,
            settingType = obtainString(R.string.random_setting_repeat_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_RESULT_TEXT_SIZE_TYPE,
            settingType = obtainString(R.string.random_setting_result_text_size_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_RESULT_TEXT_BOLD_TYPE,
            settingType = obtainString(R.string.random_setting_result_text_bold_text)
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
            itemType = RandomSettingData.RANDOM_BUTTON_VIBRATOR_TYPE,
            settingType = obtainString(R.string.random_setting_vibrator_text)
        ),
        RandomSettingData(
            itemType = RandomSettingData.RANDOM_BUTTON_EGGS_TYPE,
            settingType = obtainString(R.string.random_setting_eggs_text)
        )
    )

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
}