package com.maunc.randomcallname.ext

import android.app.Activity
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.AnimRes
import com.maunc.randomcallname.constant.ACTIVITY_ANIM_DEFAULT
import java.io.Serializable

fun <T> Context.startTargetActivity(clazz: Class<T>) {
    startActivity(Intent(this, clazz))
}

fun Activity.enterActivityAnim(@AnimRes enterAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, ACTIVITY_ANIM_DEFAULT)
    }
}

fun Activity.exitActivityAnim(@AnimRes exitAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, ACTIVITY_ANIM_DEFAULT, exitAnim)
    }
}

fun <T> Context.startActivityWithSerializable(
    clazz: Class<T>,
    dataMap: MutableMap<String, Serializable> = mutableMapOf(),
) {
    val bundle = Bundle()
    startActivity(Intent(this, clazz).apply {
        for ((key, value) in dataMap) {
            bundle.putSerializable(key, value)
        }
        putExtras(bundle)
    })
}

fun <T> Context.startActivityWithString(
    clazz: Class<T>,
    dataMap: MutableMap<String, String> = mutableMapOf(),
) {
    val bundle = Bundle()
    startActivity(Intent(this, clazz).apply {
        for ((key, value) in dataMap) {
            bundle.putString(key, value)
        }
        putExtras(bundle)
    })
}

fun <T> Context.startActivityWithInt(
    clazz: Class<T>,
    dataMap: MutableMap<String, Int> = mutableMapOf(),
) {
    val bundle = Bundle()
    startActivity(Intent(this, clazz).apply {
        for ((key, value) in dataMap) {
            bundle.putInt(key, value)
        }
        putExtras(bundle)
    })
}

fun <T> Context.startActivityWithAny(
    clazz: Class<T>,
    dataMap: MutableMap<String, Any> = mutableMapOf(),
) {
    val bundle = Bundle()
    startActivity(Intent(this, clazz).apply {
        for ((key, value) in dataMap) {
            if (value is Int) bundle.putInt(key, value)
            if (value is Short) bundle.putShort(key, value)
            if (value is Double) bundle.putDouble(key, value)
            if (value is Char) bundle.putChar(key, value)
            if (value is Float) bundle.putFloat(key, value)
            if (value is Byte) bundle.putByte(key, value)
            if (value is Long) bundle.putLong(key, value)
            if (value is Boolean) bundle.putBoolean(key, value)
            if (value is String) bundle.putString(key, value)
            if (value is IntArray) bundle.putIntArray(key, value)
            if (value is ShortArray) bundle.putShortArray(key, value)
            if (value is DoubleArray) bundle.putDoubleArray(key, value)
            if (value is CharArray) bundle.putCharArray(key, value)
            if (value is FloatArray) bundle.putFloatArray(key, value)
            if (value is ByteArray) bundle.putByteArray(key, value)
            if (value is LongArray) bundle.putLongArray(key, value)
            if (value is BooleanArray) bundle.putBooleanArray(key, value)
            if (value is CharSequence) bundle.putCharSequence(key, value)
            if (value is Binder) bundle.putBinder(key, value)
            if (value is Parcelable) bundle.putParcelable(key, value)
            if (value is Serializable) bundle.putSerializable(key, value)
        }
        putExtras(bundle)
    })
}