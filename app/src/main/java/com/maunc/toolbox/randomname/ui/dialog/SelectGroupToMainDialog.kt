package com.maunc.toolbox.randomname.ui.dialog

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseDialog
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.marquee
import com.maunc.toolbox.commonbase.ext.startActivityWithData
import com.maunc.toolbox.databinding.DialogSelectGroupToMainBinding
import com.maunc.toolbox.randomname.adapter.SelectGroupToMainAdapter
import com.maunc.toolbox.randomname.constant.GROUP_WITH_NAME_EXTRA
import com.maunc.toolbox.randomname.ui.activity.RandomNameMainActivity
import com.maunc.toolbox.randomname.viewmodel.SelectGroupToMainViewModel

class SelectGroupToMainDialog :
    BaseDialog<SelectGroupToMainViewModel, DialogSelectGroupToMainBinding>() {

    private val selectGroupToMainAdapter: SelectGroupToMainAdapter by lazy {
        SelectGroupToMainAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val randomNameWithGroup = data[pos]
                randomNameWithGroup.randomNameGroup.isExpand =
                    !randomNameWithGroup.randomNameGroup.isExpand
                notifyItemChanged(pos)
            }

            setOnItemLongClickListener { adpater, view, pos ->
                val randomNameWithGroup = data[pos]
                mViewModel.buttonClickLaunchVibrator()
                activity?.startActivityWithData(
                    RandomNameMainActivity::class.java,
                    mutableMapOf<String, Any>().apply {
                        put(GROUP_WITH_NAME_EXTRA, randomNameWithGroup)
                    })
                dismissAllowingStateLoss()
                return@setOnItemLongClickListener true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.selectGroupToMainViewModel = mViewModel
        mDatabind.dialogSelectGroupToMainRecycler.layoutManager = activity?.linearLayoutManager()
        mDatabind.dialogSelectGroupToMainRecycler.adapter = selectGroupToMainAdapter
        mDatabind.dialogSelectGroupToMainNoneTipsTv.marquee()
        mDatabind.dialogSelectGroupToMainTipsTv.marquee()
        mViewModel.queryGroupData()
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {
        mViewModel.groupData.observe(this) {
            if (it.isNullOrEmpty()) {
                return@observe
            }
            selectGroupToMainAdapter.setList(it)
        }
    }
}