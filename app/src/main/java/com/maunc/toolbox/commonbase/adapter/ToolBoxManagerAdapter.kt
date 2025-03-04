package com.maunc.toolbox.commonbase.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.data.ToolBoxItemData
import com.maunc.toolbox.databinding.ItemToolBoxManagerBinding

class ToolBoxManagerAdapter :
    BaseQuickAdapter<ToolBoxItemData, BaseDataBindingHolder<ItemToolBoxManagerBinding>>(R.layout.item_tool_box_manager) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemToolBoxManagerBinding>,
        item: ToolBoxItemData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemToolBoxBackgroundIcon.setImageResource(item.itemIcon)
            mDataBind.itemToolBoxTitleTv.text = item.itemTitle
        }
    }
}