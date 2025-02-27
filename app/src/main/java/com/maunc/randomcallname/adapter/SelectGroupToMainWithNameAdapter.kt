package com.maunc.randomcallname.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.randomcallname.R
import com.maunc.randomcallname.database.table.RandomNameData
import com.maunc.randomcallname.databinding.ItemSelectGroupToMainNameBinding

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