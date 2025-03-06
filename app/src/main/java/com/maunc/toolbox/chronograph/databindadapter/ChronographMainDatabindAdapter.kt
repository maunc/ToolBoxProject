package com.maunc.toolbox.chronograph.databindadapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_NONE
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_START
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_STOP
import com.maunc.toolbox.commonbase.constant.ALPHA_ONE
import com.maunc.toolbox.commonbase.constant.ALPHA_ZERO
import com.maunc.toolbox.commonbase.ext.animateToAlpha

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

    @JvmStatic
    @BindingAdapter(value = ["handleControllerRightButton"], requireAll = false)
    fun handleControllerRightButton(imageView: ImageView, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_START -> {
                imageView.setImageResource(R.drawable.icon_chronograph_start)
            }

            CHRONOGRAPH_STATUS_STOP -> {
                imageView.setImageResource(R.drawable.icon_chronograph_stop)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleControllerLeftButton"], requireAll = false)
    fun handleControllerLeftButton(imageView: ImageView, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_START -> {
                imageView.setImageResource(R.drawable.icon_chronograph_rank)
            }

            CHRONOGRAPH_STATUS_STOP -> {
                imageView.setImageResource(R.drawable.icon_chronograph_end)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleControllerLayoutAlpha"], requireAll = false)
    fun handleControllerLayoutAlpha(constraintLayout: ConstraintLayout, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_NONE -> {
                constraintLayout.animateToAlpha(
                    startAlpha = ALPHA_ONE,
                    endAlpha = ALPHA_ZERO,
                    overHide = true
                )
            }

            CHRONOGRAPH_STATUS_START -> {
                constraintLayout.animateToAlpha(
                    startAlpha = ALPHA_ZERO,
                    endAlpha = ALPHA_ONE
                )
            }

            CHRONOGRAPH_STATUS_STOP -> {

            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleStartButtonAlpha"], requireAll = false)
    fun handleStartButtonAlpha(cardView: CardView, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_NONE -> {
                cardView.animateToAlpha(
                    startAlpha = ALPHA_ZERO,
                    endAlpha = ALPHA_ONE
                )
            }

            CHRONOGRAPH_STATUS_START -> {
                cardView.animateToAlpha(
                    startAlpha = ALPHA_ONE,
                    endAlpha = ALPHA_ZERO,
                    overHide = true
                )
            }

            CHRONOGRAPH_STATUS_STOP -> {

            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleStartButtonAlpha"], requireAll = false)
    fun handleTimeTvScaleAnim(textView: TextView, isScale: Boolean) {

    }
}