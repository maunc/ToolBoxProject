package com.maunc.randomcallname.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.randomcallname.R
import com.maunc.randomcallname.database.table.RandomNameGroup
import com.maunc.randomcallname.database.table.RandomNameWithGroup
import com.maunc.randomcallname.databinding.ItemManageGroupBinding

class ManageGroupAdapter :
    BaseQuickAdapter<RandomNameWithGroup, BaseDataBindingHolder<ItemManageGroupBinding>>(R.layout.item_manage_group) {

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemManageGroupBinding>,
        item: RandomNameWithGroup,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemManageGroupNameTv.text = item.randomNameGroup.groupName
            mDataBind.itemManageGroupNameSizeTv.text = "数量:${item.randomNameDataList.size}"
        }
    }
}