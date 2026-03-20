package com.maunc.toolbox.randomname.ui.activity

import android.os.Bundle
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.obtainString
import com.maunc.base.ext.toastShort
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.commonbase.constant.COMMON_DIALOG
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.databinding.ActivityRandomBuiltinDataBinding
import com.maunc.toolbox.randomname.adapter.RandomSelectGroupAdapter
import com.maunc.toolbox.randomname.viewmodel.RandomBuiltinDataViewModel

class RandomBuiltinDataActivity :
    BaseActivity<RandomBuiltinDataViewModel, ActivityRandomBuiltinDataBinding>() {

    private val builtinSelectTipsDialog by lazy {
        CommonDialog().setTitle(obtainString(R.string.common_builtin_select_tips_tv))
    }

    private val randomBuiltinAdapter: RandomSelectGroupAdapter by lazy {
        RandomSelectGroupAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val groupData = data[pos]
                groupData.randomNameGroup.isExpand =
                    !groupData.randomNameGroup.isExpand
                notifyItemChanged(pos)
            }
            setOnItemLongClickListener { adpater, view, pos ->
                builtinSelectTipsDialog.setSureListener {
                    mViewModel.insertRandomData(data[pos]) {
                        mViewModel.selectGroup(data[pos].randomNameGroup.groupName)
                        toastShort("添加并使用“${data[pos].randomNameGroup.groupName}”成功")
                    }
                }.setCancelListener {
                    mViewModel.insertRandomData(data[pos]) {
                        toastShort("添加“${data[pos].randomNameGroup.groupName}”成功")
                    }
                }.show(supportFragmentManager, COMMON_DIALOG)
                return@setOnItemLongClickListener true
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.common_builtin_title_tv)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.randomBuiltinExpandTv.clickScale {
            randomBuiltinAdapter.data.forEachIndexed { index, itData ->
                if (itData.randomNameGroup.isExpand) {
                    itData.randomNameGroup.isExpand = false
                    randomBuiltinAdapter.notifyItemChanged(index)
                }
            }
        }
        mDatabind.randomBuiltinRecycler.layoutManager = linearLayoutManager()
        mDatabind.randomBuiltinRecycler.adapter = randomBuiltinAdapter
    }

    override fun createObserver() {
        appViewModel.randomBuiltinContentData.observeSticky(this) {
            randomBuiltinAdapter.setList(it)
        }
    }
}