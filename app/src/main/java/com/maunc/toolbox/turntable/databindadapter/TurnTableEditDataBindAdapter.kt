package com.maunc.toolbox.turntable.databindadapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.ONE_DELAY_MILLIS
import com.maunc.toolbox.commonbase.ext.obtainString

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

    @SuppressLint("SetTextI18n")
    @BindingAdapter(value = ["handleTurnTableEditSaveTextView"], requireAll = true)
    @JvmStatic
    fun handleTurnTableEditSaveTextView(
        textView: AppCompatTextView,
        currentSize: Int,
    ) {
        textView.text = "${obtainString(R.string.turn_table_new_save_data_tv)}(${currentSize}项)"
    }
}