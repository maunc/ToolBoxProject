package com.maunc.randomcallname.databindadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object WelcomeDataBindAdapter {

    @BindingAdapter(value = ["handleTargetNameTv"], requireAll = false)
    @JvmStatic
    fun handleTargetNameTv(textView: TextView, name: String) {
        textView.text = name
    }
}