package com.maunc.toolbox.commonbase.ext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.constant.GLOBAL_TAG
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
    return ContextCompat.getString(ToolBoxApplication.app, strRes)
}

fun getDimens(@DimenRes dimenRes: Int): Int {
    return ToolBoxApplication.app.resources.getDimensionPixelOffset(dimenRes)
}

fun getDimensFloat(@DimenRes dimenRes: Int): Float {
    return ToolBoxApplication.app.resources.getDimension(dimenRes)
}

fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
    return ContextCompat.getDrawable(ToolBoxApplication.app, drawableRes)
}

fun getColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(ToolBoxApplication.app, colorRes)
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
    orientation: Int = GridLayoutManager.VERTICAL,
): GridLayoutManager {
    return GridLayoutManager(this, spanCount, orientation, false)
}

fun Context.staggeredGridLayoutManager(
    spanCount: Int,
    orientation: Int = StaggeredGridLayoutManager.VERTICAL,
): StaggeredGridLayoutManager {
    return StaggeredGridLayoutManager(spanCount, orientation)
}

fun <T> MutableList<T>.mutableListInsert(vararg data: T) {
    data.forEach {
        this.add(it)
    }
}

fun RecyclerView.addCustomizeItemDecoration(
    outLeft: Int = getDimens(R.dimen.dp_15),
    outTop: Int = getDimens(R.dimen.dp_15),
    outRight: Int = getDimens(R.dimen.dp_15),
    outBottom: Int = getDimens(R.dimen.dp_15),
) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(outLeft, outTop, outRight, outBottom)
        }
    })
}