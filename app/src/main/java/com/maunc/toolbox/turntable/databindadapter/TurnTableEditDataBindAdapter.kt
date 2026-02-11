package com.maunc.toolbox.turntable.databindadapter

import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.commonbase.constant.ONE_DELAY_MILLIS

object TurnTableEditDataBindAdapter {

    @BindingAdapter(value = ["handleTurnTableEditLayout"], requireAll = true)
    @JvmStatic
    fun handleTurnTableEditLayout(
        view: View,
        handleHeight: Int,
    ) {
        view.postDelayed({
            view.updateLayoutParams<RelativeLayout.LayoutParams> {
                bottomMargin = handleHeight
            }
        }, ONE_DELAY_MILLIS)
    }
}