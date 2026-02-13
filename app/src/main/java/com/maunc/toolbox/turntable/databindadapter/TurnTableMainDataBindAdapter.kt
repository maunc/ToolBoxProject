package com.maunc.toolbox.turntable.databindadapter

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.us.mauncview.TurnTableView

@SuppressLint("SetTextI18n")
object TurnTableMainDataBindAdapter {

    @BindingAdapter(value = ["setTurnTableTitle"], requireAll = true)
    @JvmStatic
    fun setTurnTableTitle(textView: AppCompatTextView, title: String) {
        textView.text = if (title == GLOBAL_NONE_STRING) "测试标题" else title
    }

    @BindingAdapter(value = ["setTurnTableAnimSelectContent"], requireAll = true)
    @JvmStatic
    fun setTurnTableAnimSelectContent(textView: AppCompatTextView, content: String) {
        textView.text = if (content == GLOBAL_NONE_STRING) "测试选项" else content
    }

    @BindingAdapter(value = ["setTurnTableEnableTouch"], requireAll = true)
    @JvmStatic
    fun setTurnTableEnableTouch(turnTableView: TurnTableView, enableTouch: Boolean) {
        turnTableView.setEnableTouch(enableTouch)
    }
}