package com.maunc.toolbox.commonbase.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.constant.GLOBAL_TAG

fun String.logi(
    tag: String = GLOBAL_TAG,
) = Log.i(tag, this)

fun String.loge(
    tag: String = GLOBAL_TAG,
) = Log.e(tag, this)

fun Context.developmentToast() = toastShort(
    getString(R.string.functions_are_under_development)
)

fun Context.toast(text: String, time: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, time).show()

fun Context.toastLong(
    text: String,
) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun Context.toastShort(
    text: String,
) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun getString(
    @StringRes strRes: Int,
): String = ContextCompat.getString(ToolBoxApplication.app, strRes)

fun getDimens(
    @DimenRes dimenRes: Int,
): Int = ToolBoxApplication.app.resources.getDimensionPixelOffset(dimenRes)

fun getDimensFloat(
    @DimenRes dimenRes: Int,
): Float = ToolBoxApplication.app.resources.getDimension(dimenRes)

fun getDrawable(
    @DrawableRes drawableRes: Int,
): Drawable? = ContextCompat.getDrawable(ToolBoxApplication.app, drawableRes)

fun getColor(
    @ColorRes colorRes: Int,
): Int = ContextCompat.getColor(ToolBoxApplication.app, colorRes)

//获取屏幕宽度
fun Context.screenWidth() = resources.displayMetrics.widthPixels

//获取屏幕高度
fun Context.screenHeight() = resources.displayMetrics.heightPixels

fun dp2px(dp: Int): Int {
    val density = ToolBoxApplication.app.resources.displayMetrics.density
    return (dp * density + 0.5f).toInt()
}

fun px2dp(px: Int): Int {
    val density = ToolBoxApplication.app.resources.displayMetrics.density
    return (px / density + 0.5f).toInt()
}

fun Int.toDp(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        ToolBoxApplication.app.resources.displayMetrics
    ).toInt()
}

@SuppressLint("WrongConstant")
fun Context.linearLayoutManager(
    orientation: Int = LinearLayoutManager.VERTICAL,
    stackFromEnd: Boolean = false,
): LinearLayoutManager = LinearLayoutManager(this, orientation, false).apply {
    this.stackFromEnd = stackFromEnd
}

@SuppressLint("WrongConstant")
fun Context.gridLayoutManager(
    spanCount: Int,
    orientation: Int = GridLayoutManager.VERTICAL,
): GridLayoutManager = GridLayoutManager(this, spanCount, orientation, false)

fun Context.staggeredGridLayoutManager(
    spanCount: Int,
    orientation: Int = StaggeredGridLayoutManager.VERTICAL,
): StaggeredGridLayoutManager = StaggeredGridLayoutManager(spanCount, orientation)

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

/**   权限相关   */
//检验权限是否可用
fun Context.checkPermissionAvailable(
    permission: String,
): Boolean = ContextCompat.checkSelfPermission(
    this, permission
) == PackageManager.PERMISSION_GRANTED

//检验是否要去手动开启权限
fun Activity.checkPermissionManualRequest(
    permission: String,
): Boolean = !shouldShowRequestPermissionRationale(permission)

//前往当前app设置页面
fun Activity.startAppSystemSettingPage() = startActivity(
    Intent().apply {
        action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    }
)
