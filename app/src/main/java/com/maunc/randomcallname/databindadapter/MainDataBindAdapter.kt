package com.maunc.randomcallname.databindadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.maunc.randomcallname.R
import com.maunc.randomcallname.constant.RUN_STATUS_NONE
import com.maunc.randomcallname.constant.RUN_STATUS_START
import com.maunc.randomcallname.constant.RUN_STATUS_STOP
import com.maunc.randomcallname.ext.getString

object MainDataBindAdapter {

    @BindingAdapter(value = ["handleControlButtonTv"], requireAll = false)
    @JvmStatic
    fun handleControlButtonTv(textView: TextView, runStatus: Int) {
        textView.text = when (runStatus) {
            RUN_STATUS_NONE, RUN_STATUS_STOP -> {
                getString(R.string.random_start_text)
            }

            RUN_STATUS_START -> {
                getString(R.string.random_stop_text)
            }

            else -> {
                getString(R.string.random_start_text)
            }
        }
    }

    @BindingAdapter(value = ["randomNameText", "runStatus"], requireAll = true)
    @JvmStatic
    fun handleTargetNameTv(textView: TextView, randomName: String, runStatus: Int) {
        textView.text = when (runStatus) {
            RUN_STATUS_NONE -> {
                getString(R.string.random_none_text)
            }

            RUN_STATUS_START, RUN_STATUS_STOP -> {
                randomName
            }

            else -> {
                getString(R.string.random_none_text)
            }
        }
    }
}