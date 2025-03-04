package com.maunc.toolbox.randomname.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.randomname.database.table.RandomNameData
import com.maunc.toolbox.databinding.ItemManageGroupWithNameBinding

class ManageGroupWithNameAdapter :
    BaseQuickAdapter<RandomNameData, BaseDataBindingHolder<ItemManageGroupWithNameBinding>>(
        R.layout.item_manage_group_with_name
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemManageGroupWithNameBinding>,
        item: RandomNameData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemManageGroupWithNameNameTv.text = item.randomName
        }
    }
}