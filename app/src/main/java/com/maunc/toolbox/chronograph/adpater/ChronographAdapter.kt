package com.maunc.toolbox.chronograph.adpater

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.constant.timeUnitMillion
import com.maunc.toolbox.chronograph.data.ChronographData
import com.maunc.toolbox.databinding.ItemChronographBinding

@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
class ChronographAdapter :
    BaseQuickAdapter<ChronographData, BaseDataBindingHolder<ItemChronographBinding>>(R.layout.item_chronograph) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemChronographBinding>,
        item: ChronographData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemTimeIndexTv.text = "${item.index}"
            mDataBind.itemGapTimeTv.text = "+ ${item.gapTime.timeUnitMillion()}"
            mDataBind.itemTimeTv.text = item.time.timeUnitMillion()
        }
    }

    fun addChronograph(timeData: ChronographData) {
        data.add(0, timeData)
        notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    fun clearChronograph() {
        data.clear()
        notifyDataSetChanged()
    }
}