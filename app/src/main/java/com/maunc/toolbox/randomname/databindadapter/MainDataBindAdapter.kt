package com.maunc.toolbox.randomname.databindadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.R
import com.maunc.toolbox.randomname.constant.RUN_STATUS_NONE
import com.maunc.toolbox.randomname.constant.RUN_STATUS_START
import com.maunc.toolbox.randomname.constant.RUN_STATUS_STOP
import com.maunc.toolbox.commonbase.ext.obtainString

object MainDataBindAdapter {

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
}