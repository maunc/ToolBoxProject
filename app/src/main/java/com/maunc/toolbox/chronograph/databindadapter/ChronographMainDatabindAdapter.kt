package com.maunc.toolbox.chronograph.databindadapter

import android.annotation.SuppressLint
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.commonbase.ext.gone

object ChronographMainDatabindAdapter {

    private val showAlphaAnimation = AlphaAnimation(
        0f, 1f
    ).apply {
        duration = 300L
        interpolator = LinearInterpolator()
    }

    private val hideAlphaAnimation = AlphaAnimation(
        1f, 0f
    ).apply {
        duration = 300L
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    @JvmStatic
    @BindingAdapter(value = ["handleChronographText"], requireAll = false)
    fun handleChronographText(textView: TextView, timeValue: Float) {
        val minutes = (timeValue / 60).toInt() // 转换为分钟
        val remainingSeconds = timeValue % 60 // 计算剩余的秒数
        textView.text =
            String.format("%02d", minutes) + ":" + String.format("%05.2f", remainingSeconds)
    }
}