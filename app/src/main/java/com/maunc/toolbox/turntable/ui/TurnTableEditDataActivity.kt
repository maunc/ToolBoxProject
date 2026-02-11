package com.maunc.toolbox.turntable.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.utils.KeyBroadUtils
import com.maunc.toolbox.databinding.ActivityTurnTableEditDataBinding
import com.maunc.toolbox.turntable.adapter.TurnTableEditDataAdapter
import com.maunc.toolbox.turntable.constant.MAX_EDIT_DATA_NUMBER
import com.maunc.toolbox.turntable.constant.MIN_EDIT_DATA_NUMBER
import com.maunc.toolbox.turntable.viewmodel.TurnTableEditDataViewModel

/**
 * 编辑数据和新增公用页面
 */
class TurnTableEditDataActivity :
    BaseActivity<TurnTableEditDataViewModel, ActivityTurnTableEditDataBinding>() {

    private val turnTableEditDataAdapter by lazy {
        TurnTableEditDataAdapter().apply {
            setOnTurnTableEditListener(object :
                TurnTableEditDataAdapter.OnTurnTableEditDataAdapterListener {
                override fun onRemoveEditName(position: Int) {
                    if (data.size - 1 <= MIN_EDIT_DATA_NUMBER) {
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
        mDatabind.turnTableEditDataViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title_new_data_tv)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        KeyBroadUtils.registerKeyBoardHeightListener(this) {
            mViewModel.softKeyBroadHeight.postValue(it)
        }
        mDatabind.turnTableEditDataRecycler.layoutManager = linearLayoutManager()
        mDatabind.turnTableEditDataRecycler.adapter = turnTableEditDataAdapter
        mDatabind.turnTableEditSaveDataTv.clickScale {
            turnTableEditDataAdapter.data.forEach {
                Log.e("ww", "内容$it")
            }
        }
        mDatabind.turnTableEditAddDataTv.clickScale {
            if (turnTableEditDataAdapter.data.size - 1 >= MAX_EDIT_DATA_NUMBER) {
                Log.e("ww", "超量了")
                return@clickScale
            }
            turnTableEditDataAdapter.addEditNameItem(GLOBAL_NONE_STRING)
            turnTableEditDataAdapter.focusEndEditViewAndScrollToEnd()
        }
        mViewModel.initEditAdapterData(turnTableEditDataAdapter)

    }

    override fun createObserver() {

    }
}