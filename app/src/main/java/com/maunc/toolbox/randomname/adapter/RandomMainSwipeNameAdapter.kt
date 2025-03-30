package com.maunc.toolbox.randomname.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemRandomMainSwipeBinding
import com.maunc.toolbox.randomname.database.table.RandomNameData

class RandomMainSwipeNameAdapter :
    BaseQuickAdapter<RandomNameData, BaseDataBindingHolder<ItemRandomMainSwipeBinding>>(
        R.layout.item_random_main_swipe
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemRandomMainSwipeBinding>,
        item: RandomNameData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemMainSwipeNameTv.text = item.randomName
        }
    }
}