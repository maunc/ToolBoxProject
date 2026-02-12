package com.maunc.toolbox.randomname.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemRandomSelectGroupNameBinding
import com.maunc.toolbox.randomname.database.table.RandomNameData

class RandomSelectGroupWithNameAdapter :
    BaseQuickAdapter<RandomNameData, BaseDataBindingHolder<ItemRandomSelectGroupNameBinding>>(
        R.layout.item_random_select_group_name
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemRandomSelectGroupNameBinding>,
        item: RandomNameData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemManageGroupWithNameNameTv.text = item.randomName
        }
    }
}