package com.maunc.toolbox.commonbase.ext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.maunc.toolbox.ToolBoxApplication
import java.io.BufferedReader
import java.io.InputStreamReader

val gsonBuilder: Gson by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    GsonBuilder().create()
}

inline fun <reified T> String.fromJson(): T =
    gsonBuilder.fromJson(this, object : TypeToken<T>() {}.type)

inline fun <reified T> T.toJson(): String = gsonBuilder.toJson(this)

inline fun <reified T> assetFileParseJson(fileName: String): T {
    val stringBuilder = StringBuilder()
    ToolBoxApplication.app.assets.open(fileName).use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        }
    }
    return stringBuilder.toString().fromJson<T>()
}