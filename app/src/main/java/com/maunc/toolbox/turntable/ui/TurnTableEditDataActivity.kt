package com.maunc.toolbox.turntable.ui

import android.annotation.SuppressLint
import android.os.Bundle
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
                @SuppressLint("NotifyDataSetChanged")
                override fun onRemoveEditName(position: Int) {
                    if (data.size <= MIN_EDIT_DATA_NUMBER) {
                        mViewModel.showErrorTips(obtainString(R.string.turn_table_edit_data_error_tips_one_tv))
                        return
                    }
                    data.removeAt(position)
                    notifyDataSetChanged()
                    if (position == data.size) {
                        //删除的是最后一个
                        focusEditViewToPosition(position - 1)
                    } else {
                        focusEditViewToPosition(position)
                    }
                    mViewModel.handleCurrentSaveSize(data.size - 1)
                }

                override fun onEditAfterTextChanged(position: Int, text: String) {
                    mViewModel.hideErrorTips()
                }

                override fun onEditSpaceAfterTextChanged(position: Int) {
                    mViewModel.showErrorTips(obtainString(R.string.turn_table_edit_data_error_tips_four_tv))
                }
            })
        }
    }

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
        mDatabind.turnTableEditDataRecycler.itemAnimator = null
        mDatabind.turnTableEditDataRecycler.layoutManager = linearLayoutManager()
        mDatabind.turnTableEditDataRecycler.adapter = turnTableEditDataAdapter
        mDatabind.turnTableEditSaveDataTv.clickScale {
            turnTableEditDataAdapter.data.forEach { editData ->
                if (editData.content.isEmpty()) {
                    mViewModel.showErrorTips(obtainString(R.string.turn_table_edit_data_error_tips_three_tv))
                    return@clickScale
                }
            }
            mViewModel.insertTurnTableEditData(turnTableEditDataAdapter.data)
        }
        mDatabind.turnTableEditAddDataTv.clickScale {
            if (turnTableEditDataAdapter.data.size >= MAX_EDIT_DATA_NUMBER) {
                mViewModel.showErrorTips(obtainString(R.string.turn_table_edit_data_error_tips_two_tv))
                return@clickScale
            }
            turnTableEditDataAdapter.addEditNameItem(GLOBAL_NONE_STRING)
            // 将最后一个输入框获取焦点并移动
            turnTableEditDataAdapter.focusEditViewToPosition(turnTableEditDataAdapter.data.size - 1)
            mViewModel.handleCurrentSaveSize(turnTableEditDataAdapter.data.size - 1)
        }
        mViewModel.initEditAdapterNotData(turnTableEditDataAdapter)

    }

    override fun createObserver() {

    }
}