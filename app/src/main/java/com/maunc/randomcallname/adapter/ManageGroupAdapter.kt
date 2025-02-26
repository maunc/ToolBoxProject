package com.maunc.randomcallname.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.randomcallname.R
import com.maunc.randomcallname.database.table.RandomNameGroup
import com.maunc.randomcallname.databinding.ItemManageGroupBinding

class ManageGroupAdapter :
    BaseQuickAdapter<RandomNameGroup, BaseDataBindingHolder<ItemManageGroupBinding>>(R.layout.item_manage_group) {

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemManageGroupBinding>,
        item: RandomNameGroup,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemManageGroupNameTv.text = item.groupName
        }
    }
}