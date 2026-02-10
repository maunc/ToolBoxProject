package com.maunc.toolbox.commonbase.ext

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.DownloadManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.SearchManager
import android.app.UiModeManager
import android.app.job.JobScheduler
import android.content.ClipboardManager
import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.storage.StorageManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.maunc.toolbox.ToolBoxApplication

/**
 * Return system service which type is [T]
 */
inline fun <reified T> Context.getSystemService(): T? =
    ContextCompat.getSystemService(this, T::class.java)

val Context.windowManager get() = getSystemService<WindowManager>()
val Context.clipboardManager get() = getSystemService<ClipboardManager>()
val Context.layoutInflater get() = getSystemService<LayoutInflater>()
val Context.activityManager get() = getSystemService<ActivityManager>()
val Context.powerManager get() = getSystemService<PowerManager>()
val Context.alarmManager get() = getSystemService<AlarmManager>()
val Context.notificationManager get() = getSystemService<NotificationManager>()
val Context.keyguardManager get() = getSystemService<KeyguardManager>()
val Context.locationManager get() = getSystemService<LocationManager>()
val Context.searchManager get() = getSystemService<SearchManager>()
val Context.storageManager get() = getSystemService<StorageManager>()
val Context.vibrator get() = getSystemService<Vibrator>()
val Context.vibratorManager get() = getSystemService<VibratorManager>()
val Context.connectivityManager get() = getSystemService<ConnectivityManager>()
val Context.wifiManager get() = getSystemService<WifiManager>()
val Context.audioManager get() = getSystemService<AudioManager>()
val Context.mediaRouter get() = getSystemService<MediaRouter>()
val Context.telephonyManager get() = getSystemService<TelephonyManager>()
val Context.sensorManager get() = getSystemService<SensorManager>()
val Context.subscriptionManager get() = getSystemService<SubscriptionManager>()
val Context.carrierConfigManager get() = getSystemService<CarrierConfigManager>()
val Context.inputMethodManager get() = getSystemService<InputMethodManager>()
val Context.uiModeManager get() = getSystemService<UiModeManager>()
val Context.downloadManager get() = getSystemService<DownloadManager>()
val Context.batteryManager get() = getSystemService<BatteryManager>()
val Context.jobScheduler get() = getSystemService<JobScheduler>()
val Context.accessibilityManager get() = getSystemService<AccessibilityManager>()


//震动时间
const val DEFAULT_VIBRATOR_TIME = 10L

//震动幅度
const val DEFAULT_RECORD_TOUCH_AMPLITUDE = 1
fun launchVibrator(
    milliseconds: Long = DEFAULT_VIBRATOR_TIME,
    amplitude: Int = DEFAULT_RECORD_TOUCH_AMPLITUDE,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = ToolBoxApplication.app.vibratorManager
        val vibrationEffect =
            VibrationEffect.createOneShot(milliseconds, amplitude)
        vibratorManager?.defaultVibrator?.vibrate(vibrationEffect)
    } else {
        val vibrator = ToolBoxApplication.app.vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect =
                VibrationEffect.createOneShot(milliseconds, amplitude)
            vibrator?.vibrate(vibrationEffect)
        } else {
            vibrator?.vibrate(milliseconds)
        }
    }
}

//展示和收起键盘的间隔
const val DELAY_KEY_BROAD = 100L
fun showSoftInputKeyBoard(
    editText: EditText,
    delayMillis: Long = DELAY_KEY_BROAD,
) {
    editText.postDelayed({
        editText.requestFocusable()
        val inputManger = ToolBoxApplication.app.inputMethodManager
        inputManger?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }, delayMillis)
}

fun hideSoftInputKeyBoard(
    editText: EditText,
    delayMillis: Long = DELAY_KEY_BROAD,
) {
    editText.postDelayed({
        val inputManger = ToolBoxApplication.app.inputMethodManager
        inputManger?.hideSoftInputFromWindow(editText.windowToken, 0)
    }, delayMillis)
}

/**
 * 强制隐藏软键盘（适配特殊场景，比如键盘未绑定到某个 View）
 */
fun forceHideKeyboard(view: View) {
    val imm = ToolBoxApplication.app.inputMethodManager ?: return
    // 关闭所有激活的输入法窗口
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}