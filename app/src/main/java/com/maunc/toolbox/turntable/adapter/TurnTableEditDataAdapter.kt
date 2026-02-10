package com.maunc.toolbox.turntable.adapter

import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.showSoftInputKeyBoard
import com.maunc.toolbox.commonbase.ext.spaceProhibitedInput
import com.maunc.toolbox.turntable.data.TurnTableEditData

class TurnTableEditDataAdapter :
    BaseMultiItemQuickAdapter<TurnTableEditData, BaseViewHolder>() {

    init {
        addItemType(
            TurnTableEditData.EDIT_TURN_TABLE_TITLE,
            R.layout.item_edit_turn_table_title_data
        )
        addItemType(
            TurnTableEditData.EDIT_TURN_TABLE_NAME,
            R.layout.item_edit_turn_table_data
        )
    }

    override fun convert(holder: BaseViewHolder, item: TurnTableEditData) {
        // 如果所有item的所有值都一样那么hashCode就会一样
        // 所以再TurnTableEditData中加了一当前时间保证hashCode一定不一样
        val itemPosition = getItemPosition(item)
        val haveView = holder.itemView
        val nameEditView =
            haveView.findViewById<AppCompatEditText>(R.id.item_turn_table_edit_data_name_edit)
        nameEditView.spaceProhibitedInput()
        nameEditView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showSoftInputKeyBoard(v as AppCompatEditText)
            }
        }
        nameEditView.setOnClickListener {
            it.requestFocus()
            showSoftInputKeyBoard(it as AppCompatEditText)
        }
        when (item.itemType) {
            TurnTableEditData.EDIT_TURN_TABLE_TITLE -> {}

            TurnTableEditData.EDIT_TURN_TABLE_NAME -> {
                val removeEditItemTv =
                    haveView.findViewById<AppCompatImageView>(R.id.item_turn_table_edit_data_delete_edit)
                removeEditItemTv.setOnClickListener {
                    onTurnTableEditListener?.onRemoveEditName(itemPosition)
                }
            }
        }
    }

    /**
     * 添加标题布局
     */
    fun addEditTitleItem(content: String) = addEditItem(
        TurnTableEditData(content, TurnTableEditData.EDIT_TURN_TABLE_TITLE)
    )

    /**
     * 添加选项布局
     */
    fun addEditNameItem(content: String) = addEditItem(
        TurnTableEditData(content, TurnTableEditData.EDIT_TURN_TABLE_NAME)
    )

    private fun addEditItem(editData: TurnTableEditData) {
        data.add(editData)
        notifyItemInserted(this.data.size - 1)
        recyclerView.scrollToPosition(this.data.size - 1)
    }

    private var onTurnTableEditListener: OnTurnTableEditDataAdapterListener? = null

    interface OnTurnTableEditDataAdapterListener {
        fun onRemoveEditName(position: Int)
    }

    fun setOnTurnTableEditListener(onTurnTableEditListener: OnTurnTableEditDataAdapterListener) {
        this.onTurnTableEditListener = onTurnTableEditListener
    }
}