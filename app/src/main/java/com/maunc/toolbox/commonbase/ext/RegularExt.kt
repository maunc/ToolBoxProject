package com.maunc.toolbox.commonbase.ext

import java.util.regex.Pattern

fun Char.isChineseChar() = this.code in 0x4E00..0x9FFF

fun Char.isHexDigitCompat() = this in '0'..'9' || this in 'a'..'f'

fun String.isPhoneNumber() = Pattern.compile(
    "^1[3-9]\\d{9}$"
).matcher(this).matches()

fun String.isEmail() = Pattern.compile(
    "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
).matcher(this).matches()

fun String.isUrl() = Pattern.compile(
    "^(https?|ftp)://[^\\\\s/\$.?#].[^\\\\s]*\$"
).matcher(this).matches()