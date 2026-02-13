package com.maunc.toolbox.turntable.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemTurnTableConfigColorBinding
import com.maunc.toolbox.turntable.data.TurnTableConfigColorData

class TurnTableConfigColorAdapter :
    BaseQuickAdapter<TurnTableConfigColorData, BaseDataBindingHolder<ItemTurnTableConfigColorBinding>>(
        R.layout.item_turn_table_config_color
    ) {

    private var currentSelectIndex = -1
    override fun convert(
        holder: BaseDataBindingHolder<ItemTurnTableConfigColorBinding>,
        item: TurnTableConfigColorData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemTurnTableConfigColorTestTurnTable.setHideBoard(true)
            mDataBind.itemTurnTableConfigColorTestTurnTable.setCircleRadius(150f)
            mDataBind.itemTurnTableConfigColorTestTurnTable.setTurnTableColor(item.colorList)
            mDataBind.itemTurnTableConfigColorFlag.visibleOrGone(currentSelectIndex == holder.layoutPosition)
        }
    }

    fun setCurrentSelectIndex(index: Int) {
        this.currentSelectIndex = index
    }
}