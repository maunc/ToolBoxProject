package com.maunc.toolbox.ext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.RandomNameApplication
import com.maunc.toolbox.base.BaseViewModel
import com.maunc.toolbox.constant.GLOBAL_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun String.logi(tag: String = GLOBAL_TAG) {
    Log.i(tag, this)
}

fun String.loge(tag: String = GLOBAL_TAG) {
    Log.e(tag, this)
}

fun Context.developmentToast() {
    Toast.makeText(this, getString(R.string.functions_are_under_development), Toast.LENGTH_SHORT)
        .show()
}

fun Context.toast(text: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, time).show()
}

fun Context.toastLong(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.toastShort(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

/**
 *  调用携程
 */
fun <T> BaseViewModel<*>.launch(
    block: () -> T,
    success: (T) -> Unit = {},
    error: (Throwable) -> Unit = {},
) {
    viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            success(it)
        }.onFailure {
            error(it)
        }
    }
}

fun getString(@StringRes strRes: Int): String {
    return ContextCompat.getString(RandomNameApplication.app, strRes)
}

fun getDimens(@DimenRes dimenRes: Int): Int {
    return RandomNameApplication.app.resources.getDimensionPixelOffset(dimenRes)
}

fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
    return ContextCompat.getDrawable(RandomNameApplication.app, drawableRes)
}

fun getColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(RandomNameApplication.app, colorRes)
}

@SuppressLint("WrongConstant")
fun Context.linearLayoutManager(
    orientation: Int = LinearLayoutManager.VERTICAL,
): LinearLayoutManager {
    return LinearLayoutManager(this, orientation, false)
}

@SuppressLint("WrongConstant")
fun Context.gridLayoutManager(
    spanCount: Int,
    orientation: Int = GridLayoutManager.HORIZONTAL,
): GridLayoutManager {
    return GridLayoutManager(this, spanCount, orientation, false)
}

fun Context.staggeredGridLayoutManager(
    spanCount: Int,
    orientation: Int = StaggeredGridLayoutManager.VERTICAL,
): StaggeredGridLayoutManager {
    return StaggeredGridLayoutManager(spanCount, orientation)
}