package com.maunc.randomcallname.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.randomcallname.R
import com.maunc.randomcallname.adapter.ManageGroupWithNameAdapter
import com.maunc.randomcallname.base.BaseActivity
import com.maunc.randomcallname.constant.COMMON_DIALOG
import com.maunc.randomcallname.constant.GROUP_NAME_EXTRA
import com.maunc.randomcallname.constant.RESULT_SOURCE_FROM_MANAGE_GROUP_WITH_NAME_PAGE
import com.maunc.randomcallname.constant.RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE
import com.maunc.randomcallname.constant.RESULT_SOURCE_FROM_NONE_PAGE
import com.maunc.randomcallname.constant.WHETHER_DATA_HAS_CHANGE
import com.maunc.randomcallname.databinding.ActivityManageGroupWithNameBinding
import com.maunc.randomcallname.ext.clickScale
import com.maunc.randomcallname.ext.finishCurrentResultToActivity
import com.maunc.randomcallname.ext.linearLayoutManager
import com.maunc.randomcallname.ext.obtainActivityIntentPutData
import com.maunc.randomcallname.ext.obtainIntentPutData
import com.maunc.randomcallname.ui.dialog.CommonDialog
import com.maunc.randomcallname.viewmodel.ManageGroupWithNameViewModel

/**
 * 管理分组下名称页面
 */
class ManageGroupWithNameActivity :
    BaseActivity<ManageGroupWithNameViewModel, ActivityManageGroupWithNameBinding>() {

    private var mGroupName: String? = null

    private var otherPageEchoSources = RESULT_SOURCE_FROM_NONE_PAGE

    private val manageGroupWithNameActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        otherPageEchoSources = it.resultCode
        val dataIntent = it.data
        //从新增名称页面回来
        if (otherPageEchoSources == RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE) {
            dataIntent?.extras?.getBoolean(WHETHER_DATA_HAS_CHANGE)?.let { isChange ->
                mViewModel.whetherDataHasChange.value = isChange
                if (isChange) {
                    mGroupName?.let { groupName ->
                        mViewModel.queryGroupWithNameData(groupName)
                    }
                }
            }
        }
    }

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            baseFinishCurrentActivity()
        }
    }

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
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            baseFinishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            startNewNameActivity()
        }
        mDatabind.manageGroupWithNameNewGroupTv.clickScale {
            startNewNameActivity()
        }
        mDatabind.commonToolBar.commonToolBarTitleTv.text = mGroupName
        mDatabind.manageGroupWithNameRecycler.layoutManager =
            linearLayoutManager(LinearLayoutManager.VERTICAL)
        mDatabind.manageGroupWithNameRecycler.adapter = manageGroupWithNameAdapter
        onBackPressedDispatcher.addCallback(backPressCallback)
        mDatabind.manageGroupWithNameRefreshLayout.setOnRefreshListener {
            mGroupName?.let {
                mViewModel.queryGroupWithNameData(it)
            }
        }
        mGroupName?.let {
            mViewModel.queryGroupWithNameData(it)
        }
    }

    override fun createObserver() {
        mViewModel.groupData.observe(this) {
            mDatabind.manageGroupWithNameRefreshLayout.finishRefresh()
            if (it.isNullOrEmpty()) {
                return@observe
            }
            manageGroupWithNameAdapter.setList(it)
            if (otherPageEchoSources == RESULT_SOURCE_FROM_NEW_NAME_WITH_GROUP_PAGE) {
                mDatabind.manageGroupWithNameRecycler.smoothScrollToPosition(it.size)
            }
            otherPageEchoSources = RESULT_SOURCE_FROM_NONE_PAGE
        }
    }

    private fun startNewNameActivity() {
        manageGroupWithNameActivityResult.launch(
            obtainActivityIntentPutData(
                NewNameWithGroupActivity::class.java,
                mutableMapOf<String, Any>().apply {
                    mGroupName?.let { startGroupName ->
                        put(GROUP_NAME_EXTRA, startGroupName)
                    }
                }
            )
        )
    }

    private fun baseFinishCurrentActivity(action: () -> Unit = {}) {
        action()
        finishCurrentResultToActivity(
            resultCode = RESULT_SOURCE_FROM_MANAGE_GROUP_WITH_NAME_PAGE,
            intent = obtainIntentPutData(mutableMapOf<String, Any>().apply {
                put(WHETHER_DATA_HAS_CHANGE, mViewModel.whetherDataHasChange.value!!)
            })
        )
    }
}