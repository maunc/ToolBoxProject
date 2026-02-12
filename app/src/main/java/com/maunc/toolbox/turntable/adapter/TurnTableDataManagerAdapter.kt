package com.maunc.toolbox.turntable.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.visibleOrGone
import com.maunc.toolbox.databinding.ItemTurnTableManagerGroupBinding
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup
import java.text.SimpleDateFormat
import java.util.Date

class TurnTableDataManagerAdapter :
    BaseQuickAdapter<TurnTableNameWithGroup, BaseDataBindingHolder<ItemTurnTableManagerGroupBinding>>(
        R.layout.item_turn_table_manager_group
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemTurnTableManagerGroupBinding>,
        item: TurnTableNameWithGroup,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemTurnTableSelectNameTv.text = item.turnTableGroupData.groupName
            mDataBind.itemTurnTableSelectFlag.visibleOrGone(item.turnTableGroupData.isSelect)
            mDataBind.itemTurnTableSelectSizeTv.text = "数量:${item.turnTableNameDataList.size}"
            mDataBind.itemTurnTableSelectCreateTimeTv.text =
                "添加时间:${convertTime(item.turnTableGroupData.createTime)}"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTime(time: Long): String {
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(time))
    }
}