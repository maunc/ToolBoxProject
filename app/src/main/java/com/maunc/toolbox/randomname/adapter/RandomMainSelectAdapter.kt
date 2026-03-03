package com.maunc.toolbox.randomname.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemRandomMainSelectBinding
import com.maunc.toolbox.randomname.database.table.RandomNameData

/**
 * 已点和未点名单
 */
class RandomMainSelectAdapter :
    BaseQuickAdapter<RandomNameData, BaseDataBindingHolder<ItemRandomMainSelectBinding>>(R.layout.item_random_main_select) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemRandomMainSelectBinding>,
        item: RandomNameData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemRandomMainSelectName.text = item.randomName
        }
    }
}

