package com.maunc.randomcallname.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.randomcallname.R
import com.maunc.randomcallname.database.table.RandomNameWithGroup
import com.maunc.randomcallname.databinding.ItemSelectGroupToMainBinding
import com.maunc.randomcallname.ext.linearLayoutManager
import com.maunc.randomcallname.ext.visibleOrGone

class SelectGroupToMainAdapter :
    BaseQuickAdapter<RandomNameWithGroup, BaseDataBindingHolder<ItemSelectGroupToMainBinding>>(
        R.layout.item_select_group_to_main
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemSelectGroupToMainBinding>,
        item: RandomNameWithGroup,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemSelectToMainNameTv.text = item.randomNameGroup.groupName
            mDataBind.itemSelectToMainSizeTv.text = "数量:${item.randomNameDataList.size}"
            mDataBind.itemSelectToMainNameRecycler.layoutManager = context.linearLayoutManager()
            val toMainWithNameAdapter = SelectGroupToMainWithNameAdapter()
            mDataBind.itemSelectToMainNameRecycler.adapter = toMainWithNameAdapter
            toMainWithNameAdapter.setList(item.randomNameDataList)
            mDataBind.itemSelectToMainNameRecycler.visibleOrGone(item.randomNameGroup.isExpand)
            mDataBind.itemSelectToMainExpandIv.setImageResource(
                if (item.randomNameGroup.isExpand) {
                    R.drawable.icon_group_expand_yes
                } else {
                    R.drawable.icon_group_expand_no
                }
            )
        }
    }
}