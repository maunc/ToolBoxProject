package com.maunc.toolbox.randomname.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.randomname.data.RandomSettingData

class RandomSettingViewModel : BaseRandomNameViewModel<BaseModel>() {

    private var settingItemData = MutableLiveData<MutableList<RandomSettingData>>(mutableListOf())

    fun initRecyclerData(): MutableList<RandomSettingData> {
        settingItemData.value?.mutableListInsert(
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_SLEEP_TYPE,
                settingType = obtainString(R.string.random_setting_sleep_text)
            ),
            RandomSettingData(
                itemType = RandomSettingData.RANDOM_BUTTON_CLICK_VIBRATOR_TYPE,
                settingType = obtainString(R.string.random_setting_vibrator_text)
            )
        )
        return settingItemData.value!!
    }
}