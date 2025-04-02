package com.maunc.toolbox.randomname.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.data.convertTime
import com.maunc.toolbox.databinding.ItemManageGroupWithNameBinding
import com.maunc.toolbox.randomname.database.table.RandomNameData
import java.text.SimpleDateFormat
import java.util.Date

class ManageGroupWithNameAdapter :
    BaseQuickAdapter<RandomNameData, BaseDataBindingHolder<ItemManageGroupWithNameBinding>>(
        R.layout.item_manage_group_with_name
    ) {

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemManageGroupWithNameBinding>,
        item: RandomNameData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemManageGroupWithNameNameTv.text = item.randomName
            mDataBind.itemManageGroupWithNameInsertTimeTv.text =
                "添加时间:${convertTime(item.insertNameTime)}"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTime(time: Long): String {
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(time))
    }
}