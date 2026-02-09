package com.maunc.toolbox.randomname.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.randomname.database.table.RandomNameData
import com.maunc.toolbox.databinding.ItemSelectGroupToMainNameBinding

class RandomSelectGroupWithNameAdapter :
    BaseQuickAdapter<RandomNameData, BaseDataBindingHolder<ItemSelectGroupToMainNameBinding>>(
        R.layout.item_select_group_to_main_name
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSelectGroupToMainNameBinding>,
        item: RandomNameData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemManageGroupWithNameNameTv.text = item.randomName
        }
    }
}