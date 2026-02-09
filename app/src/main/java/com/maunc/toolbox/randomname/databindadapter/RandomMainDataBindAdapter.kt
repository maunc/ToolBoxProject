package com.maunc.toolbox.randomname.databindadapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.randomname.constant.RANDOM_AUTO
import com.maunc.toolbox.randomname.constant.RANDOM_MANUAL
import com.maunc.toolbox.randomname.constant.RANDOM_NOW
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MAX
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MEDIUM
import com.maunc.toolbox.randomname.constant.RANDOM_SPEED_MIN
import com.maunc.toolbox.randomname.constant.RUN_STATUS_NONE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_START
import com.maunc.toolbox.randomname.constant.RUN_STATUS_STOP

@SuppressLint("SetTextI18n")
object RandomMainDataBindAdapter {

    @BindingAdapter(value = ["handleRandomResultTextBold"], requireAll = false)
    @JvmStatic
    fun handleRandomResultTextBold(textView: TextView, isBold: Boolean) {
        if (isBold) {
            textView.typeface = Typeface.DEFAULT_BOLD
        } else {
            textView.typeface = Typeface.DEFAULT
        }
    }

    @BindingAdapter(value = ["handleControlButtonTv"], requireAll = false)
    @JvmStatic
    fun handleControlButtonTv(textView: TextView, runStatus: Int) {
        textView.text = when (runStatus) {
            RUN_STATUS_NONE, RUN_STATUS_STOP -> obtainString(R.string.random_start_text)
            RUN_STATUS_START -> obtainString(R.string.random_stop_text)
            else -> obtainString(R.string.random_start_text)
        }
    }

    @BindingAdapter(value = ["randomNameText", "runStatus"], requireAll = true)
    @JvmStatic
    fun handleTargetNameTv(textView: TextView, randomName: String, runStatus: Int) {
        textView.text = when (runStatus) {
            RUN_STATUS_NONE -> obtainString(R.string.random_none_text)
            RUN_STATUS_START, RUN_STATUS_STOP -> randomName
            else -> obtainString(R.string.random_none_text)
        }
    }

    @BindingAdapter(value = ["showRandomTipsText"], requireAll = true)
    @JvmStatic
    fun showRandomTipsText(textView: TextView, randomTip: String) {
        textView.visibleOrGone(randomTip != GLOBAL_NONE_STRING)
        textView.text = randomTip
    }

    @BindingAdapter(value = ["showMainSwipeSpeedTv"], requireAll = false)
    @JvmStatic
    fun showMainSwipeSpeedTv(textView: TextView, speed: Long) {
        val speedString = when (speed) {
            RANDOM_SPEED_MIN -> obtainString(R.string.random_setting_speed_one_text)
            RANDOM_SPEED_MEDIUM -> obtainString(R.string.random_setting_speed_two_text)
            RANDOM_SPEED_MAX -> obtainString(R.string.random_setting_speed_three_text)
            else -> GLOBAL_NONE_STRING
        }
        textView.text = "当前速度:$speedString"
    }

    @BindingAdapter(value = ["showMainSwipeRandomTypeTv"], requireAll = false)
    @JvmStatic
    fun showMainSwipeRandomTypeTv(textView: TextView, randomType: Int) {
        val randomTypeString = when (randomType) {
            RANDOM_NOW -> obtainString(R.string.random_setting_random_type_now_text)
            RANDOM_AUTO -> obtainString(R.string.random_setting_random_type_auto_text)
            RANDOM_MANUAL -> obtainString(R.string.random_setting_random_type_manual_text)
            else -> GLOBAL_NONE_STRING
        }
        textView.text = "当前模式:$randomTypeString"
    }
}