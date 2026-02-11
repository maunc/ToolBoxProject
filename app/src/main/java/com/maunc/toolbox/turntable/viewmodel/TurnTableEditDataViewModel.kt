package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.turntable.adapter.TurnTableEditDataAdapter

class TurnTableEditDataViewModel : BaseViewModel<BaseModel>() {

    var softKeyBroadHeight = MutableLiveData<Int>() //软键盘高度

    fun initEditAdapterData(adapter: TurnTableEditDataAdapter) {
        for (i in 0 until 2) {
            if (i == 0) {
                adapter.addEditTitleItem(GLOBAL_NONE_STRING)
            }
            adapter.addEditNameItem(GLOBAL_NONE_STRING)
        }
    }
}