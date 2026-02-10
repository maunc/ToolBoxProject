package com.maunc.toolbox.turntable.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.forceHideKeyboard
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.databinding.ActivityEditTurnTableDataBinding
import com.maunc.toolbox.turntable.adapter.TurnTableEditDataAdapter
import com.maunc.toolbox.turntable.constant.MIN_EDIT_DATA_NUMBER
import com.maunc.toolbox.turntable.viewmodel.EditTurnTableDataViewModel

/**
 * 编辑数据和新增公用页面
 */
class EditTurnTableDataActivity :
    BaseActivity<EditTurnTableDataViewModel, ActivityEditTurnTableDataBinding>() {

    private val turnTableEditDataAdapter by lazy {
        TurnTableEditDataAdapter().apply {
            setOnTurnTableEditListener(object :
                TurnTableEditDataAdapter.OnTurnTableEditDataAdapterListener {
                override fun onRemoveEditName(position: Int) {
                    Log.e("ww", "$position")
                    if (data.size <= MIN_EDIT_DATA_NUMBER) {
                        // TODO 可做提示
                        Log.e("ww", "不能删除")
                        return
                    }
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, data.size)
                }
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title_new_data_tv)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.turnTableEditDataRecycler.layoutManager = linearLayoutManager()
        mDatabind.turnTableEditDataRecycler.adapter = turnTableEditDataAdapter
        mDatabind.turnTableEditDataRecycler.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val touchView = (v as RecyclerView).findChildViewUnder(event.x, event.y)
                if (touchView !is AppCompatEditText) {
                    clearRecyclerViewEditFocusAndHideKeyBoard()
                }
            }
            // 保证隐藏后还能再点击
            return@setOnTouchListener false
        }
        // 列表不足以盛满屏幕触摸到主视图也取消软键盘
        mDatabind.turnTableEditSmartLayout.setOnTouchListener { _, _ ->
            clearRecyclerViewEditFocusAndHideKeyBoard()
            return@setOnTouchListener true
        }
        mDatabind.turnTableEditSaveDataTv.clickScale {

        }
        mDatabind.turnTableEditAddDataTv.clickScale {
            turnTableEditDataAdapter.addEditNameItem(GLOBAL_NONE_STRING)
        }
        mViewModel.initEditAdapterData(turnTableEditDataAdapter)

    }

    override fun createObserver() {

    }

    /**
     * 隐藏软键盘并取消所有输入框的焦点
     */
    private fun clearRecyclerViewEditFocusAndHideKeyBoard() {
        forceHideKeyboard(mDatabind.main)
        if (mDatabind.turnTableEditDataRecycler.childCount <= 0) return
        for (i in 0 until mDatabind.turnTableEditDataRecycler.childCount) {
            val itemView = mDatabind.turnTableEditDataRecycler.getChildAt(i)
            val editText =
                itemView.findViewById<AppCompatEditText>(R.id.item_turn_table_edit_data_name_edit)
            editText.clearFocus()
        }
    }
}