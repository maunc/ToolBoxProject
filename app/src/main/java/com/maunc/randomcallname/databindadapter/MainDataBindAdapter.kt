package com.maunc.randomcallname.databindadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.maunc.randomcallname.RandomNameApplication
import com.maunc.randomcallname.R
import com.maunc.randomcallname.constant.RUN_STATUS_NONE
import com.maunc.randomcallname.constant.RUN_STATUS_START
import com.maunc.randomcallname.constant.RUN_STATUS_STOP

object MainDataBindAdapter {

    @BindingAdapter(value = ["handleControlButtonTv"], requireAll = false)
    @JvmStatic
    fun handleControlButtonTv(textView: TextView, runStatus: Int) {
        textView.text = when (runStatus) {
            RUN_STATUS_NONE, RUN_STATUS_STOP -> {
                RandomNameApplication.app.getString(R.string.random_start_text)
            }

            RUN_STATUS_START -> {
                RandomNameApplication.app.getString(R.string.random_stop_text)
            }

            else -> {
                RandomNameApplication.app.getString(R.string.random_start_text)
            }
        }
    }

    @BindingAdapter(value = ["handleTargetNameTv"], requireAll = false)
    @JvmStatic
    fun handleTargetNameTv(textView: TextView, name: String) {
        textView.text = name
    }
}