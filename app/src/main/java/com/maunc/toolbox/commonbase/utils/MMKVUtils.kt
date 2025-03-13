package com.maunc.toolbox.commonbase.utils

import com.tencent.mmkv.MMKV

val obtainMMKV: MMKVUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    MMKVUtils()
}

//默认值
private const val DEFAULT_STRING = ""
private const val DEFAULT_INT = -1

/** 随机名称用到的 */
//随机速度
const val randomSpeed = "RandomSpeed"

class MMKVUtils {

    fun getString(
        key: String
    ): String? = MMKV.defaultMMKV().decodeString(key, DEFAULT_STRING)

    fun getInt(
        key: String
    ): Int = MMKV.defaultMMKV().decodeInt(key, DEFAULT_INT)

    fun getLong(
        key: String
    ): Long = MMKV.defaultMMKV().decodeLong(key)

    fun getBoolean(
        key: String,
    ): Boolean = MMKV.defaultMMKV().decodeBool(key, false)

    fun putLong(
        key: String,
        value: Long,
    ) = MMKV.defaultMMKV().encode(key, value)

    fun putBoolean(
        key: String,
        value: Boolean
    ) = MMKV.defaultMMKV().encode(key, value)

    fun putString(
        key: String,
        value: String
    ) = MMKV.defaultMMKV().encode(key, value)

    fun putInt(
        key: String,
        value: Int
    ) = MMKV.defaultMMKV().encode(key, value)
}
