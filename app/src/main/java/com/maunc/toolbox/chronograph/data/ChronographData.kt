package com.maunc.toolbox.chronograph.data

data class ChronographData(
    var index: Int,//名次
    var time: Long,//使用时间
    var gapTime: Long,//与上一名时间差距
)