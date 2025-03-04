package com.maunc.toolbox.randomname.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.randomname.adapter.ManageGroupAdapter
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.randomname.constant.COMMON_DIALOG
import com.maunc.toolbox.randomname.constant.GROUP_NAME_EXTRA
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_MANAGE_GROUP_WITH_NAME_PAGE
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_NEW_GROUP_PAGE
import com.maunc.toolbox.randomname.constant.RESULT_SOURCE_FROM_NONE_PAGE
import com.maunc.toolbox.randomname.constant.WHETHER_DATA_HAS_CHANGE
import com.maunc.toolbox.databinding.ActivityManageGroupBinding
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainActivityIntent
import com.maunc.toolbox.commonbase.ext.obtainActivityIntentPutData
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.randomname.viewmodel.ManageGroupViewModel

/**
 * 管理分组页面
 */
@SuppressLint("NotifyDataSetChanged")
class ManageGroupActivity : BaseActivity<ManageGroupViewModel, ActivityManageGroupBinding>() {

    private var otherPageEchoSources = RESULT_SOURCE_FROM_NONE_PAGE

    private val manageGroupActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        otherPageEchoSources = it.resultCode
        val dataIntent = it.data
        when (otherPageEchoSources) {
            //从新增分组页面回来,从管理名称页面回来
            RESULT_SOURCE_FROM_NEW_GROUP_PAGE, RESULT_SOURCE_FROM_MANAGE_GROUP_WITH_NAME_PAGE -> {
                dataIntent?.let { intent ->
                    intent.extras?.getBoolean(WHETHER_DATA_HAS_CHANGE)?.let { isChange ->
                        if (isChange) {
                            mViewModel.queryGroupData()
                        }
                    }
                }
            }
        }
    }

    private val manageGroupAdapter: ManageGroupAdapter by lazy {
        ManageGroupAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val randomNameWithGroup = data[pos]
                manageGroupActivityResult.launch(
                    obtainActivityIntentPutData(
                        ManageGroupWithNameActivity::class.java,
                        mutableMapOf<String, Any>().apply {
                            put(GROUP_NAME_EXTRA, randomNameWithGroup.randomNameGroup.groupName)
                        })
                )
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
            manageGroupActivityResult.launch(obtainActivityIntent(NewRandomGroupActivity::class.java))
        }
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.manage_group_page_title_text)
        mDatabind.manageGroupNewGroupTv.clickScale {
            manageGroupActivityResult.launch(obtainActivityIntent(NewRandomGroupActivity::class.java))
        }
        mDatabind.manageGroupRecycler.layoutManager =
            linearLayoutManager(LinearLayoutManager.VERTICAL)
        mDatabind.manageGroupRecycler.adapter = manageGroupAdapter
        mDatabind.manageGroupRefreshLayout.setOnRefreshListener {
            mViewModel.queryGroupData()
        }
        mViewModel.queryGroupData()
    }

    override fun createObserver() {
        mViewModel.groupData.observe(this) {
            mDatabind.manageGroupRefreshLayout.finishRefresh()
            if (it.isNullOrEmpty()) {
                return@observe
            }
            manageGroupAdapter.setList(it)
            if (otherPageEchoSources == RESULT_SOURCE_FROM_NEW_GROUP_PAGE) {
                mDatabind.manageGroupRecycler.smoothScrollToPosition(it.size)
            }
            otherPageEchoSources = RESULT_SOURCE_FROM_NONE_PAGE
        }
    }
}