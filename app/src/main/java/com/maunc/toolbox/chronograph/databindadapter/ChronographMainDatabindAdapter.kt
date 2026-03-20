package com.maunc.toolbox.chronograph.databindadapter

import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.maunc.base.ext.animateToAlpha
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_NONE
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_PAUSE
import com.maunc.toolbox.chronograph.constant.CHRONOGRAPH_STATUS_START
import com.maunc.toolbox.commonbase.constant.ALPHA_ONE
import com.maunc.toolbox.commonbase.constant.ALPHA_ZERO

object ChronographMainDatabindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["handleControllerRightButton"], requireAll = false)
    fun handleControllerRightButton(imageView: ImageView, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_START -> imageView.setImageResource(R.drawable.icon_chronograph_start)
            CHRONOGRAPH_STATUS_PAUSE -> imageView.setImageResource(R.drawable.icon_chronograph_stop)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleControllerLeftButton"], requireAll = false)
    fun handleControllerLeftButton(imageView: ImageView, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_START -> imageView.setImageResource(R.drawable.icon_chronograph_rank)
            CHRONOGRAPH_STATUS_PAUSE -> imageView.setImageResource(R.drawable.icon_chronograph_end)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleControllerLayoutAlpha"], requireAll = false)
    fun handleControllerLayoutAlpha(constraintLayout: ConstraintLayout, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_NONE -> {
                constraintLayout.animateToAlpha(
                    startAlpha = ALPHA_ONE, endAlpha = ALPHA_ZERO, overHide = true
                )
            }

            CHRONOGRAPH_STATUS_START -> {
                constraintLayout.animateToAlpha(
                    startAlpha = ALPHA_ZERO, endAlpha = ALPHA_ONE
                )
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleStartButtonAlpha"], requireAll = false)
    fun handleStartButtonAlpha(cardView: CardView, chronographStatus: Int) {
        when (chronographStatus) {
            CHRONOGRAPH_STATUS_NONE -> {
                cardView.animateToAlpha(
                    startAlpha = ALPHA_ZERO, endAlpha = ALPHA_ONE
                )
            }

            CHRONOGRAPH_STATUS_START -> {
                cardView.animateToAlpha(
                    startAlpha = ALPHA_ONE, endAlpha = ALPHA_ZERO, overHide = true
                )
            }
        }
    }
}