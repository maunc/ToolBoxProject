package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.turnTableEnableTouch

class TurnTableMainViewModel : BaseViewModel<BaseModel>() {

    var turnTableIsEnableTouch = MutableLiveData(false) // 转盘是否可以触摸

    fun initViewModelConfig() {
        initSettingConfig()
    }

    fun initSettingConfig(
        enableTouch: Boolean = obtainMMKV.getBoolean(turnTableEnableTouch),
    ) {
        turnTableIsEnableTouch.value = enableTouch
    }
}