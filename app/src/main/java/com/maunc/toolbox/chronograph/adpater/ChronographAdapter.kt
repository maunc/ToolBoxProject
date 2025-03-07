package com.maunc.toolbox.chronograph.adpater

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.chronograph.data.ChronographData
import com.maunc.toolbox.databinding.ItemChronographBinding

@SuppressLint("SetTextI18n", "NotifyDataSetChanged", "DefaultLocale")
class ChronographAdapter :
    BaseQuickAdapter<ChronographData, BaseDataBindingHolder<ItemChronographBinding>>(R.layout.item_chronograph) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemChronographBinding>,
        item: ChronographData,
    ) {
        holder.dataBinding?.let { mDataBind ->
            mDataBind.itemTimeIndexTv.text = "${item.index}"
            mDataBind.itemGapTimeTv.text = "+ ${formatTime(item.gapTime)}"
            mDataBind.itemTimeTv.text = formatTime(item.time)
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

    private fun formatTime(timeValue: Float): String {
        val minutes = (timeValue / 60).toInt() // 转换为分钟
        val remainingSeconds = timeValue % 60 // 计算剩余的秒数
        return String.format("%02d", minutes) + ":" + String.format("%05.2f", remainingSeconds)
    }
}