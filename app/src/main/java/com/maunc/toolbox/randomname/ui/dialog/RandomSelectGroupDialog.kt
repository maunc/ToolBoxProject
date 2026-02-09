package com.maunc.toolbox.randomname.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseDialog
import com.maunc.toolbox.commonbase.database.randomGroupDao
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.marquee
import com.maunc.toolbox.databinding.DialogSelectGroupBinding
import com.maunc.toolbox.randomname.adapter.SelectGroupToMainAdapter
import com.maunc.toolbox.randomname.viewmodel.RandomSelectGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("NotifyDataSetChanged")
class RandomSelectGroupDialog :
    BaseDialog<RandomSelectGroupViewModel, DialogSelectGroupBinding>() {

    private val selectGroupToMainAdapter: SelectGroupToMainAdapter by lazy {
        SelectGroupToMainAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val randomNameWithGroup = data[pos]
                randomNameWithGroup.randomNameGroup.isExpand =
                    !randomNameWithGroup.randomNameGroup.isExpand
                notifyItemChanged(pos)
            }

            setOnItemLongClickListener { adpater, view, pos ->
                mViewModel.buttonClickLaunchVibrator()
                val group = data[pos].randomNameGroup
                lifecycleScope.launch(Dispatchers.IO) {
                    randomGroupDao.selectGroup(group.groupName)
                    withContext(Dispatchers.Main) {
                        dismissAllowingStateLoss()
                    }
                }
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
        mDatabind.dialogSelectGroupToMainTipsTv.marquee()
        mDatabind.dialogSelectGroupToMainBackIv.clickScale {
            dismissAllowingStateLoss()
        }
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