package com.maunc.toolbox.chronograph.data

data class ChronographData(
    var index: Int,//名次
    var time: Float,//使用时间
    var gapTime: Float,//与上一名时间差距
)