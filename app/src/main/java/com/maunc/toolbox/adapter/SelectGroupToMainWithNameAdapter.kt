package com.maunc.toolbox.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.database.table.RandomNameData
import com.maunc.toolbox.databinding.ItemSelectGroupToMainNameBinding

class SelectGroupToMainWithNameAdapter :
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