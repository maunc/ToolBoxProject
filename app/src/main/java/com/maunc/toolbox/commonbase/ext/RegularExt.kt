package com.maunc.toolbox.commonbase.ext

import java.util.regex.Pattern

fun Char.isChineseChar(): Boolean = this.code in 0x4E00..0x9FFF

fun String.isPhoneNumber(): Boolean =
    Pattern.compile("^1[3-9]\\d{9}$")
        .matcher(this).matches()

fun String.isEmail(): Boolean =
    Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
        .matcher(this).matches()

fun String.isUrl(): Boolean =
    Pattern.compile("^(https?|ftp)://[^\\\\s/\$.?#].[^\\\\s]*\$")
        .matcher(this).matches()