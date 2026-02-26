package com.maunc.toolbox

import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.utils.obtainMMKV

class ToolBoxApplicationViewModel : BaseViewModel<BaseModel>() {

    fun initMMKV() {
        // 初始化mmkv所有默认配置
        obtainMMKV.init()
    }
}