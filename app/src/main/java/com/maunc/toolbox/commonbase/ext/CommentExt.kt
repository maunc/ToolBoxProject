package com.maunc.toolbox.commonbase.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
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

fun developmentToast() = toastShort(
    obtainString(R.string.functions_are_under_development)
)

fun toast(text: String, time: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(ToolBoxApplication.app, text, time).show()

fun toastLong(
    text: String,
) = Toast.makeText(ToolBoxApplication.app, text, Toast.LENGTH_LONG).show()

fun toastShort(
    text: String,
) = Toast.makeText(ToolBoxApplication.app, text, Toast.LENGTH_SHORT).show()

fun obtainString(
    @StringRes strRes: Int,
): String = ContextCompat.getString(ToolBoxApplication.app, strRes)

fun obtainDimens(
    @DimenRes dimenRes: Int,
): Int = ToolBoxApplication.app.resources.getDimensionPixelOffset(dimenRes)

fun obtainDimensFloat(
    @DimenRes dimenRes: Int,
): Float = ToolBoxApplication.app.resources.getDimension(dimenRes)

fun obtainDrawable(
    @DrawableRes drawableRes: Int,
): Drawable? = ContextCompat.getDrawable(ToolBoxApplication.app, drawableRes)

fun obtainColor(
    @ColorRes colorRes: Int,
): Int = ContextCompat.getColor(ToolBoxApplication.app, colorRes)

fun obtainColorToARAG(
    a: Int = 255, r: Int = 0, g: Int = 0, b: Int = 0,
)= Color.argb(a, r, g, b)

fun obtainColorStateList(
    @ColorRes colorRes: Int,
) = ContextCompat.getColorStateList(ToolBoxApplication.app, colorRes)

fun obtainAppName(): String? {
    val pm: PackageManager = ToolBoxApplication.app.packageManager
    try {
        val packageInfo: PackageInfo =
            pm.getPackageInfo(ToolBoxApplication.app.packageName, 0)
        val applicationInfo = packageInfo.applicationInfo
        val labelRes = applicationInfo?.labelRes
        return labelRes?.let {
            ToolBoxApplication.app.resources.getString(it)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return null
}

fun obtainVersionName(): String? {
    try {
        val packageManager: PackageManager = ToolBoxApplication.app.packageManager
        val packageInfo: PackageInfo = packageManager.getPackageInfo(
            ToolBoxApplication.app.packageName, 0
        )
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return null
}

fun obtainAppVersionCode(): Int {
    var versionCode = 0
    try {
        val pm: PackageManager = ToolBoxApplication.app.packageManager
        val pi: PackageInfo = pm.getPackageInfo(ToolBoxApplication.app.packageName, 0)
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.longVersionCode.toInt()
        } else {
            pi.versionCode
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return versionCode
}

//获取屏幕宽度
fun Context.obtainScreenWidth() = resources.displayMetrics.widthPixels

//获取屏幕高度
fun Context.obtainScreenHeight() = resources.displayMetrics.heightPixels

/**
 * 获取屏幕高度
 * 可以选择是否带状态栏
 */
@SuppressLint("InternalInsetResource")
fun Context.obtainScreenHeight(isAddStatusBar: Boolean): Int {
    val heightPixels = resources.displayMetrics.heightPixels
    if (isAddStatusBar) {
        var statusBarHeight = 0
        val resourceId =
            resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return heightPixels + statusBarHeight
    }
    return heightPixels
}

fun dp2px(dp: Int): Int =
    (dp * ToolBoxApplication.app.resources.displayMetrics.density + 0.5f).toInt()

fun px2dp(px: Int): Int =
    (px / ToolBoxApplication.app.resources.displayMetrics.density + 0.5f).toInt()

fun Int.toDp(): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    ToolBoxApplication.app.resources.displayMetrics
).toInt()

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
    outLeft: Int = obtainDimens(R.dimen.dp_15),
    outTop: Int = obtainDimens(R.dimen.dp_15),
    outRight: Int = obtainDimens(R.dimen.dp_15),
    outBottom: Int = obtainDimens(R.dimen.dp_15),
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
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    }
)

fun Activity.startAllFileSettingPage() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            startActivity(
                Intent().apply {
                    action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    data = Uri.parse("package:" + applicationContext.packageName)
                }
            )
        }
    } else {
        toastShort(obtainString(R.string.android_version_not_support_text))
    }
}
