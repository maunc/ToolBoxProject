package com.maunc.randomcallname.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.randomcallname.R
import com.maunc.randomcallname.adapter.ManageGroupAdapter
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.COMMON_DIALOG
import com.maunc.randomcallname.constant.GROUP_NAME_EXTRA
import com.maunc.randomcallname.databinding.ActivityManageGroupBinding
import com.maunc.randomcallname.ext.clickScale
import com.maunc.randomcallname.ext.linearLayoutManager
import com.maunc.randomcallname.ext.loge
import com.maunc.randomcallname.ext.startActivityWithString
import com.maunc.randomcallname.ext.startTargetActivity
import com.maunc.randomcallname.ui.dialog.CommonDialog
import com.maunc.randomcallname.viewmodel.ManageGroupViewModel

@SuppressLint("NotifyDataSetChanged")
class ManageGroupActivity : BaseActivity<ManageGroupViewModel, ActivityManageGroupBinding>() {

    private val manageGroupAdapter: ManageGroupAdapter by lazy {
        ManageGroupAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val randomNameGroup = data[pos]
                startActivityWithString(
                    ManageGroupWithNameActivity::class.java,
                    mutableMapOf<String, String>().apply {
                        put(GROUP_NAME_EXTRA, randomNameGroup.groupName)
                    })
            }
            setOnItemLongClickListener { adapter, view, pos ->
                val randomNameGroup = data[pos]
                val commonDialogTitle = "是否删除:${randomNameGroup.groupName}？"
                CommonDialog().setTitle(commonDialogTitle)
                    .setSureListener {
                        mViewModel.deleteGroupData(randomNameGroup.groupName)
                    }.show(supportFragmentManager, COMMON_DIALOG)
                return@setOnItemLongClickListener true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.manageGroupViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_new_group)
        mDatabind.commonToolBar.commonToolBarBackButton.setOnClickListener {
            finish()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.setOnClickListener {
            startTargetActivity(NewGroupActivity::class.java)
        }
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.manage_group_page_title_text)
        mDatabind.manageGroupNewGroupTv.clickScale {
            startTargetActivity(NewGroupActivity::class.java)
        }
        mDatabind.manageGroupRecycler.layoutManager =
            linearLayoutManager(LinearLayoutManager.VERTICAL)
        mDatabind.manageGroupRecycler.adapter = manageGroupAdapter
    }

    override fun createObserver() {
        mViewModel.groupData.observe(this) {
            if (it.isNullOrEmpty()) {
                return@observe
            }
            manageGroupAdapter.setList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.queryGroupData()
    }
}