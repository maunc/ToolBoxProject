package com.maunc.randomcallname.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.randomcallname.R
import com.maunc.randomcallname.adapter.ManageGroupWithNameAdapter
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.COMMON_DIALOG
import com.maunc.randomcallname.constant.GROUP_NAME_EXTRA
import com.maunc.randomcallname.databinding.ActivityManageGroupWithNameBinding
import com.maunc.randomcallname.ext.clickScale
import com.maunc.randomcallname.ext.linearLayoutManager
import com.maunc.randomcallname.ext.loge
import com.maunc.randomcallname.ext.startActivityWithString
import com.maunc.randomcallname.ext.startTargetActivity
import com.maunc.randomcallname.ui.dialog.CommonDialog
import com.maunc.randomcallname.viewmodel.ManageGroupWithNameViewModel

class ManageGroupWithNameActivity :
    BaseActivity<ManageGroupWithNameViewModel, ActivityManageGroupWithNameBinding>() {

    private var mGroupName: String? = null

    private val manageGroupWithNameAdapter: ManageGroupWithNameAdapter by lazy {
        ManageGroupWithNameAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val randomNameGroupWithName = data[pos]
            }
            setOnItemLongClickListener { adapter, view, pos ->
                val randomNameGroupWithName = data[pos]
                val commonDialogTitle = "是否删除:${randomNameGroupWithName.randomName}？"
                CommonDialog().setTitle(commonDialogTitle)
                    .setSureListener {
                        mViewModel.deleteGroupWithNameData(
                            randomNameGroupWithName.toGroupName,
                            randomNameGroupWithName.randomName
                        )
                    }.show(supportFragmentManager, COMMON_DIALOG)
                return@setOnItemLongClickListener true
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mGroupName = intent?.extras?.getString(GROUP_NAME_EXTRA)
        mDatabind.manageGroupWithNameViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_new_group)
        mDatabind.commonToolBar.commonToolBarBackButton.setOnClickListener {
            finish()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setOnClickListener {
            startActivityWithString(
                NewNameWithGroupActivity::class.java,
                mutableMapOf<String, String>().apply {
                    mGroupName?.let { startGroupName -> put(GROUP_NAME_EXTRA, startGroupName) }
                })
        }
        mDatabind.manageGroupWithNameNewGroupTv.clickScale {
            startActivityWithString(
                NewNameWithGroupActivity::class.java,
                mutableMapOf<String, String>().apply {
                    mGroupName?.let { startGroupName -> put(GROUP_NAME_EXTRA, startGroupName) }
                })
        }
        mDatabind.commonToolBar.commonToolBarTitleTv.text = mGroupName
        mDatabind.manageGroupWithNameRecycler.layoutManager =
            linearLayoutManager(LinearLayoutManager.VERTICAL)
        mDatabind.manageGroupWithNameRecycler.adapter = manageGroupWithNameAdapter
    }

    override fun createObserver() {
        mViewModel.groupData.observe(this) {
            if (it.isNullOrEmpty()) {
                return@observe
            }
            manageGroupWithNameAdapter.setList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mGroupName?.let {
            mViewModel.queryGroupWithNameData(it)
        }
    }
}