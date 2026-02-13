package com.maunc.toolbox.turntable.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemTurnTableBuiltinGroupBinding
import com.maunc.toolbox.databinding.ItemTurnTableBuiltinGroupNameBinding
import com.maunc.toolbox.turntable.database.table.TurnTableNameData
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

class TurnTableBuiltinAdapter :
    BaseQuickAdapter<TurnTableNameWithGroup, BaseDataBindingHolder<ItemTurnTableBuiltinGroupBinding>>(
        R.layout.item_turn_table_builtin_group
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemTurnTableBuiltinGroupBinding>,
        item: TurnTableNameWithGroup,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemTurnTableBuiltinGroupNameTv.text = item.turnTableGroupData.groupName
            mDataBind.itemTurnTableBuiltinGroupSizeTv.text =
                "数量:${item.turnTableNameDataList.size}"
            mDataBind.itemTurnTableBuiltinGroupNameRecycler.layoutManager =
                context.linearLayoutManager()
            val turnBuiltinNameAdapter = TurnTableBuiltinNameAdapter()
            mDataBind.itemTurnTableBuiltinGroupNameRecycler.adapter = turnBuiltinNameAdapter
            turnBuiltinNameAdapter.setList(item.turnTableNameDataList)
            mDataBind.itemTurnTableBuiltinGroupNameRecycler.visibleOrGone(item.turnTableGroupData.isExpand)
            mDataBind.itemSelectExpandIv.setImageResource(
                if (item.turnTableGroupData.isExpand) {
                    R.drawable.icon_group_expand_yes
                } else {
                    R.drawable.icon_group_expand_no
                }
            )
        }
    }
}

class TurnTableBuiltinNameAdapter :
    BaseQuickAdapter<TurnTableNameData, BaseDataBindingHolder<ItemTurnTableBuiltinGroupNameBinding>>(
        R.layout.item_turn_table_builtin_group_name
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemTurnTableBuiltinGroupNameBinding>,
        item: TurnTableNameData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemTurnTableBuiltinNameTv.text = item.name
        }
    }
}