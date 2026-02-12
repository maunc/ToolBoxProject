package com.maunc.toolbox.commonbase.ext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

val gsonBuilder: Gson by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    GsonBuilder().create()
}

inline fun <reified T> String.fromJson(): T =
    gsonBuilder.fromJson(this, object : TypeToken<T>() {}.type)

inline fun <reified T> T.toJson(): String = gsonBuilder.toJson(this)