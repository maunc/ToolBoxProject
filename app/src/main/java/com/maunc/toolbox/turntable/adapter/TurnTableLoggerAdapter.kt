package com.maunc.toolbox.turntable.adapter

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.turntable.data.TurnTableLoggerData

class TurnTableLoggerAdapter : BaseMultiItemQuickAdapter<TurnTableLoggerData, BaseViewHolder>() {
    init {
        addItemType(TurnTableLoggerData.TURN_TABLE_TIPS_LOG, R.layout.item_turn_table_tips_log)
        addItemType(TurnTableLoggerData.TURN_TABLE_RESULT_LOG, R.layout.item_turn_table_result_log)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: TurnTableLoggerData) {
        val itemPosition = getItemPosition(item)
        val haveView = holder.itemView
        when (item.itemType) {
            TurnTableLoggerData.TURN_TABLE_TIPS_LOG -> {
                val tipsTextView =
                    haveView.findViewById<AppCompatTextView>(R.id.item_turn_table_tips)
                tipsTextView.text = item.result
            }

            TurnTableLoggerData.TURN_TABLE_RESULT_LOG -> {
                val resultTextView =
                    haveView.findViewById<AppCompatTextView>(R.id.item_turn_table_result)
                resultTextView.text = "你抽中了:${item.result}"
            }
        }
    }

    fun addTipsLogger(tips: String) = addLogger(
        TurnTableLoggerData(
            result = tips,
            itemType = TurnTableLoggerData.TURN_TABLE_TIPS_LOG
        )
    )

    fun addResultLogger(result: String) = addLogger(
        TurnTableLoggerData(
            result = result,
            itemType = TurnTableLoggerData.TURN_TABLE_RESULT_LOG
        )
    )

    private fun addLogger(loggerData: TurnTableLoggerData) {
        data.add(loggerData)
        notifyItemInserted(this.data.size - 1)
        recyclerView.scrollToPosition(this.data.size - 1)
    }
}