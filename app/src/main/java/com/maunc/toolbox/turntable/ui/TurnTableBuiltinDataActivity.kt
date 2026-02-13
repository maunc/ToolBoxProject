package com.maunc.toolbox.turntable.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_DIALOG
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.toastShort
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.databinding.ActivityTurnTableBuiltinDataBinding
import com.maunc.toolbox.turntable.adapter.TurnTableBuiltinAdapter
import com.maunc.toolbox.turntable.viewmodel.TurnTableBuiltinDataViewModel

class TurnTableBuiltinDataActivity :
    BaseActivity<TurnTableBuiltinDataViewModel, ActivityTurnTableBuiltinDataBinding>() {

    private val builtinSelectTipsDialog by lazy {
        CommonDialog().setTitle(obtainString(R.string.turn_table_builtin_select_tips_tv))
    }

    private val turnTableBuiltinAdapter by lazy {
        TurnTableBuiltinAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val groupData = data[pos]
                groupData.turnTableGroupData.isExpand =
                    !groupData.turnTableGroupData.isExpand
                notifyItemChanged(pos)
            }

            setOnItemLongClickListener { adpater, view, pos ->
                builtinSelectTipsDialog.setSureListener {
                    mViewModel.insertTurnTableEditData(data[pos]) {
                        mViewModel.selectGroup(data[pos].turnTableGroupData.groupName)
                        toastShort("添加“${data[pos].turnTableGroupData.groupName}”成功")
                    }
                }.setCancelListener {
                    mViewModel.insertTurnTableEditData(data[pos]) {
                        toastShort("添加“${data[pos].turnTableGroupData.groupName}”成功")
                    }
                }.show(supportFragmentManager, COMMON_DIALOG)
                return@setOnItemLongClickListener true
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_builtin_title_tv)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.turnTableBuiltinExpandTv.clickScale {
            turnTableBuiltinAdapter.data.forEachIndexed { index, itData ->
                if (itData.turnTableGroupData.isExpand) {
                    itData.turnTableGroupData.isExpand = false
                    turnTableBuiltinAdapter.notifyItemChanged(index)
                }
            }
        }
        mDatabind.turnTableBuiltinRecycler.layoutManager = linearLayoutManager()
        mDatabind.turnTableBuiltinRecycler.adapter = turnTableBuiltinAdapter
        mViewModel.initBuiltinData()
    }

    override fun createObserver() {
        mViewModel.builtinDataLiveData.observe(this) {
            turnTableBuiltinAdapter.setList(it)
        }
    }
}