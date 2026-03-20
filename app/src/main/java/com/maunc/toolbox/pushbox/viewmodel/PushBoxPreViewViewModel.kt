package com.maunc.toolbox.pushbox.viewmodel

import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.pushbox.constant.allGradesMapData
import com.maunc.toolbox.pushbox.data.PushBoxGradleData

class PushBoxPreViewViewModel : BaseViewModel<BaseModel>() {

    fun initPreViewResultList(): MutableList<PushBoxGradleData> {
        val resultList = mutableListOf<PushBoxGradleData>()
        allGradesMapData.forEachIndexed { index, arrayLists ->
            resultList.add(PushBoxGradleData(arrayLists, index))
        }
        return resultList
    }
}