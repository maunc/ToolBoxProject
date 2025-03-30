package com.maunc.toolbox.randomname.viewmodel

import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.launchVibrator
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.utils.randomButtonClickVibrator
import com.maunc.toolbox.randomname.constant.mediaPlayer

open class BaseRandomNameViewModel<M : BaseModel?> : BaseViewModel<BaseModel>() {

    fun buttonClickLaunchVibrator() {
        if (obtainMMKV.getBoolean(randomButtonClickVibrator)) launchVibrator()
    }

    fun buttonClickSoundEffect() {
        if (obtainMMKV.getBoolean(randomButtonClickVibrator)) {
            mediaPlayer.start()
        }
    }
}