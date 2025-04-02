package com.maunc.toolbox.randomname.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.databinding.ItemManageGroupBinding
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup
import java.text.SimpleDateFormat
import java.util.Date

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
            mDataBind.itemManageGroupNameInsertTimeTv.text =
                "添加时间:${convertTime(item.randomNameGroup.insertGroupTime)}"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTime(time: Long): String {
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(time))
    }
}