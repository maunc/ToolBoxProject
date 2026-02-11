package com.maunc.toolbox.turntable.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.ONE_DELAY_MILLIS
import com.maunc.toolbox.commonbase.ext.addEditTextListener
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

    // 避免实际内容被刷掉
    private var isSetEditViewText = false

    // 当前点击的EditView的位置
    private var hasFocusEditViewPosition = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun convert(holder: BaseViewHolder, item: TurnTableEditData) {
        // 如果所有item的所有值都一样那么hashCode就会一样
        // 所以再TurnTableEditData中加了一当前时间保证hashCode一定不一样
        val itemPosition = holder.layoutPosition
        val haveView = holder.itemView
        val nameEditView =
            haveView.findViewById<AppCompatEditText>(R.id.item_turn_table_edit_data_name_edit)
        isSetEditViewText = true
        nameEditView.setText(item.content)
        isSetEditViewText = false
        nameEditView.spaceProhibitedInput()
        // 拦截触摸事件,避免RecyclerView滑动的时候弹出软键盘导致体验差
        nameEditView.setOnTouchListener { view, event ->
            return@setOnTouchListener false
        }
        nameEditView.setOnFocusChangeListener { v, hasFocus ->
            if (v.isClickable || v.isPressed) {
                // 如果是点击获取的焦点,将输入索引更改为最新的
                hasFocusEditViewPosition = itemPosition
            }
        }
        nameEditView.setOnClickListener { view ->
            // 会优先FocusChangeListener 这个点击只有第二次会回调
            hasFocusEditViewPosition = itemPosition
            showSoftInputKeyBoard(view as AppCompatEditText)
        }
        nameEditView.addEditTextListener(afterTextChanged = { editContent ->
            if (isSetEditViewText || itemPosition == RecyclerView.NO_POSITION || holder.layoutPosition >= data.size) {
                return@addEditTextListener
            }
            if (hasFocusEditViewPosition == -1 || itemPosition != hasFocusEditViewPosition) {
                return@addEditTextListener
            }
            data[itemPosition].let { currentItem ->
                if (currentItem.content != editContent) {
                    currentItem.content = editContent
                }
            }
        })
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
     * 将最后一个输入框获取焦点并移动
     */
    fun focusEndEditViewAndScrollToEnd() {
        clearRecyclerViewEditFocus()
        //childCount 返回的是所有可见View组成的下标(最大值代表可见View的范围)
        //findFirstVisibleItemPosition,findLastVisibleItemPosition这种获取的是可见的View在adapter的位置
        if (data.isEmpty()) return
        val lastPosition = data.size - 1
        recyclerView.postDelayed({
            recyclerView.scrollToPosition(lastPosition)
            val targetHolder =
                recyclerView.findViewHolderForAdapterPosition(lastPosition) ?: return@postDelayed
            val editView = obtainRecyclerItemEditView(targetHolder.itemView)
            editView.requestFocus()
        }, ONE_DELAY_MILLIS)
    }

    /**
     * 取消所有可见输入框的焦点
     */
    fun clearRecyclerViewEditFocus() {
        if (recyclerView.childCount <= 0) return
        for (i in 0 until recyclerView.childCount) {
            val itemView = recyclerView.getChildAt(i)
            val editText = obtainRecyclerItemEditView(itemView)
            editText.clearFocus()
        }
    }

    private fun obtainRecyclerItemEditView(view: View): AppCompatEditText {
        return view.findViewById(R.id.item_turn_table_edit_data_name_edit)
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