package com.maunc.toolbox.commonbase.ext

fun Char.isChineseChar(): Boolean {
    val codePoint = this.code
    return codePoint in 0x4E00..0x9FFF
}