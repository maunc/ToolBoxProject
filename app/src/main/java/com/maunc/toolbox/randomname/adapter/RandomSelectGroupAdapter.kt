package com.maunc.toolbox.randomname.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemRandomSelectGroupBinding
import com.maunc.toolbox.randomname.database.table.RandomNameWithGroup

class RandomSelectGroupAdapter :
    BaseQuickAdapter<RandomNameWithGroup, BaseDataBindingHolder<ItemRandomSelectGroupBinding>>(
        R.layout.item_random_select_group
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemRandomSelectGroupBinding>,
        item: RandomNameWithGroup,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemSelectNameTv.text = item.randomNameGroup.groupName
            mDataBind.itemSelectSizeTv.text = "数量:${item.randomNameDataList.size}"
            mDataBind.itemSelectNameRecycler.layoutManager = context.linearLayoutManager()
            mDataBind.itemSelectSelectFlag.visibleOrGone(item.randomNameGroup.isSelect)
            val toMainWithNameAdapter = RandomSelectGroupWithNameAdapter()
            mDataBind.itemSelectNameRecycler.adapter = toMainWithNameAdapter
            toMainWithNameAdapter.setList(item.randomNameDataList)
            mDataBind.itemSelectNameRecycler.visibleOrGone(item.randomNameGroup.isExpand)
            mDataBind.itemSelectExpandIv.setImageResource(
                if (item.randomNameGroup.isExpand) {
                    R.drawable.icon_group_expand_yes
                } else {
                    R.drawable.icon_group_expand_no
                }
            )
        }
    }
}