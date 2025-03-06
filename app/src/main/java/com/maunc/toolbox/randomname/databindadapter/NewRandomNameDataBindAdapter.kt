package com.maunc.toolbox.randomname.databindadapter

import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object NewRandomNameDataBindAdapter {

    /**
     * 旋转动画
     */
    private val rotateAnimation = RotateAnimation(
        0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
        RotateAnimation.RELATIVE_TO_SELF, 0.5f
    ).apply {
        duration = 500L
        repeatCount = -1
    }

    @BindingAdapter(value = ["handleRotateAnimation"], requireAll = false)
    @JvmStatic
    fun handleRotateAnimation(imageView: ImageView, runStatus: Boolean) {
        if (runStatus) {
            imageView.startAnimation(rotateAnimation)
        } else {
            imageView.clearAnimation()
        }
    }
}