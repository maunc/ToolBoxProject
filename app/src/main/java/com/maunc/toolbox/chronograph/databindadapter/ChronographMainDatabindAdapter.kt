package com.maunc.toolbox.chronograph.databindadapter

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter

object ChronographMainDatabindAdapter {

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