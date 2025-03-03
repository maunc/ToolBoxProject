package com.maunc.toolbox.chronograph.data

data class ChronographData(
    var index: Int,//名次
    var time: String,//使用时间
    var gapTime: String,//与上一名时间差距
)
