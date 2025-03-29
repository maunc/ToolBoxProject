package com.maunc.toolbox.commonbase.ext

import android.app.Activity
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

//ActivityAnimDefault
const val ACTIVITY_ANIM_DEFAULT = -1

/**========================================  Activity转场动画  ========================================*/
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

/**========================================  Intent跳转  ========================================*/
fun <T> Context.startTargetActivity(
    clazz: Class<T>
) = startActivity(obtainActivityIntent(clazz))

fun <T> Context.startActivityWithData(
    clazz: Class<T>,
    dataMap: MutableMap<String, Any> = mutableMapOf(),
) = startActivity(obtainActivityIntentPutData(clazz, dataMap))

/**========================================  Intent构造  ========================================*/
fun <T> Context.obtainActivityIntent(
    clazz: Class<T>,
): Intent = Intent(this, clazz)

fun obtainIntentPutData(
    dataMap: MutableMap<String, Any> = mutableMapOf(),
): Intent = intentPutData(Intent(), dataMap)

fun <T> Context.obtainActivityIntentPutData(
    clazz: Class<T>,
    dataMap: MutableMap<String, Any> = mutableMapOf(),
): Intent = intentPutData(Intent(this, clazz), dataMap)

fun intentPutData(
    intent: Intent,
    dataMap: MutableMap<String, Any> = mutableMapOf(),
): Intent {
    val bundle = Bundle()
    return intent.apply {
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
    }
}

/**========================================  Activity销毁  ========================================*/
fun AppCompatActivity.finishCurrentActivity(
    @AnimRes exitAnim: Int = ACTIVITY_ANIM_DEFAULT,
    action: () -> Unit = {},
) {
    action()
    finish()
    if (exitAnim != ACTIVITY_ANIM_DEFAULT) {
        exitActivityAnim(exitAnim)
    }
}

fun AppCompatActivity.finishCurrentResultToActivity(
    resultCode: Int = RESULT_OK,
    intent: Intent? = null,
    @AnimRes exitAnim: Int = ACTIVITY_ANIM_DEFAULT,
    action: () -> Unit = {},
) {
    action()
    if (intent == null) {
        setResult(resultCode)
    } else {
        setResult(resultCode, intent)
    }
    finish()
    if (exitAnim != ACTIVITY_ANIM_DEFAULT) {
        exitActivityAnim(exitAnim)
    }
}

