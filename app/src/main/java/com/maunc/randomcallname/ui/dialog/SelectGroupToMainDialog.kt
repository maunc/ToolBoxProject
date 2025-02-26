package com.maunc.randomcallname.ui.dialog

import android.os.Bundle
import com.maunc.randomcallname.R
import com.maunc.randomcallname.adapter.SelectGroupToMainAdapter
import com.maunc.randomcallname.base.BaseDialog
import com.maunc.randomcallname.constant.GROUP_WITH_NAME_EXTRA
import com.maunc.randomcallname.databinding.DialogSelectGroupToMainBinding
import com.maunc.randomcallname.ext.linearLayoutManager
import com.maunc.randomcallname.ext.startActivityWithAny
import com.maunc.randomcallname.ui.activity.MainActivity
import com.maunc.randomcallname.viewmodel.SelectGroupToMainViewModel

class SelectGroupToMainDialog :
    BaseDialog<SelectGroupToMainViewModel, DialogSelectGroupToMainBinding>() {

    private val selectGroupToMainAdapter: SelectGroupToMainAdapter by lazy {
        SelectGroupToMainAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val randomNameWithGroup = data[pos]
                activity?.startActivityWithAny(
                    MainActivity::class.java,
                    mutableMapOf<String, Any>().apply {
                        put(GROUP_WITH_NAME_EXTRA, randomNameWithGroup)
                    })
                dismissAllowingStateLoss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Transparent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.selectGroupToMainViewModel = mViewModel
        mDatabind.dialogSelectGroupToMainRoot.setOnClickListener {
            dismissAllowingStateLoss()
        }
        mDatabind.dialogSelectGroupToMainRecycler.layoutManager = activity?.linearLayoutManager()
        mDatabind.dialogSelectGroupToMainRecycler.adapter = selectGroupToMainAdapter
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