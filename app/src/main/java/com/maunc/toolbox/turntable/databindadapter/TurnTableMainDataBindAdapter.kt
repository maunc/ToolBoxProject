package com.maunc.toolbox.turntable.databindadapter

import androidx.databinding.BindingAdapter
import com.us.mauncview.TurnTableView

object TurnTableMainDataBindAdapter {

    @BindingAdapter(value = ["setTurnTableEnableTouch"], requireAll = true)
    @JvmStatic
    fun setTurnTableEnableTouch(turnTableView: TurnTableView, enableTouch: Boolean) {
        turnTableView.setEnableTouch(enableTouch)
    }
}