package com.maunc.randomcallname.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.randomcallname.R
import com.maunc.randomcallname.adapter.ManageGroupAdapter
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.COMMON_DIALOG
import com.maunc.randomcallname.constant.GROUP_NAME_EXTRA
import com.maunc.randomcallname.constant.RESULT_SOURCE_FROM_NEW_GROUP_PAGE
import com.maunc.randomcallname.constant.RESULT_SOURCE_FROM_NONE_PAGE
import com.maunc.randomcallname.databinding.ActivityManageGroupBinding
import com.maunc.randomcallname.ext.clickScale
import com.maunc.randomcallname.ext.finishCurrentActivity
import com.maunc.randomcallname.ext.linearLayoutManager
import com.maunc.randomcallname.ext.startActivityWithData
import com.maunc.randomcallname.ext.startTargetActivity
import com.maunc.randomcallname.ui.dialog.CommonDialog
import com.maunc.randomcallname.viewmodel.ManageGroupViewModel

/**
 * 管理分组页面
 */
@SuppressLint("NotifyDataSetChanged")
class ManageGroupActivity : BaseActivity<ManageGroupViewModel, ActivityManageGroupBinding>() {

    private var otherPageEchoSources = RESULT_SOURCE_FROM_NONE_PAGE

    private val manageGroupActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SOURCE_FROM_NEW_GROUP_PAGE) {
            otherPageEchoSources = RESULT_SOURCE_FROM_NEW_GROUP_PAGE
            mViewModel.queryGroupData()
        }
    }

    private val manageGroupAdapter: ManageGroupAdapter by lazy {
        ManageGroupAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val randomNameWithGroup = data[pos]
                startActivityWithData(
                    ManageGroupWithNameActivity::class.java,
                    mutableMapOf<String, Any>().apply {
                        put(GROUP_NAME_EXTRA, randomNameWithGroup.randomNameGroup.groupName)
                    })
            }
            setOnItemLongClickListener { adapter, view, pos ->
                val randomNameWithGroup = data[pos]
                val commonDialogTitle = "是否删除:${randomNameWithGroup.randomNameGroup.groupName}？"
                CommonDialog().setTitle(commonDialogTitle)
                    .setSureListener {
                        mViewModel.deleteGroupData(randomNameWithGroup.randomNameGroup.groupName)
                    }.show(supportFragmentManager, COMMON_DIALOG)
                return@setOnItemLongClickListener true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.manageGroupViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_new_group)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            manageGroupActivityResult.launch(Intent(this, NewGroupActivity::class.java))
        }
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.manage_group_page_title_text)
        mDatabind.manageGroupNewGroupTv.clickScale {
            startTargetActivity(NewGroupActivity::class.java)
        }
        mDatabind.manageGroupRecycler.layoutManager =
            linearLayoutManager(LinearLayoutManager.VERTICAL)
        mDatabind.manageGroupRecycler.adapter = manageGroupAdapter
        mViewModel.queryGroupData()
    }

    override fun createObserver() {
        mViewModel.groupData.observe(this) {
            if (it.isNullOrEmpty()) {
                return@observe
            }
            manageGroupAdapter.setList(it)
            if (otherPageEchoSources == RESULT_SOURCE_FROM_NEW_GROUP_PAGE) {
                mDatabind.manageGroupRecycler.smoothScrollToPosition(it.size)
                otherPageEchoSources = RESULT_SOURCE_FROM_NONE_PAGE
            }
        }
    }
}