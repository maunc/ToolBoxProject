package com.maunc.randomcallname.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.randomcallname.R
import com.maunc.randomcallname.database.table.RandomNameWithGroup
import com.maunc.randomcallname.databinding.ItemSelectGroupToMainBinding

class SelectGroupToMainAdapter :
    BaseQuickAdapter<RandomNameWithGroup, BaseDataBindingHolder<ItemSelectGroupToMainBinding>>(
        R.layout.item_select_group_to_main
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSelectGroupToMainBinding>,
        item: RandomNameWithGroup,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemSelectToMainNameTv.text = item.randomNameGroup.groupName
        }
    }
}