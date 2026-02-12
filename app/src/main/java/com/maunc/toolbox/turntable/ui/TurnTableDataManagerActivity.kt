package com.maunc.toolbox.turntable.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainActivityIntentPutData
import com.maunc.toolbox.commonbase.ext.obtainDimens
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.toJson
import com.maunc.toolbox.databinding.ActivityTurnTableDataManagerBinding
import com.maunc.toolbox.turntable.adapter.TurnTableDataManagerAdapter
import com.maunc.toolbox.turntable.constant.RESULT_SOURCE_FROM_TURN_NEW_TURN_TABLE_PAGE
import com.maunc.toolbox.turntable.constant.TURN_TABLE_GROUP_WITH_NAME_EXTRA
import com.maunc.toolbox.turntable.constant.TURN_TABLE_MANAGE_RECYCLER_SWIPE_DELETE
import com.maunc.toolbox.turntable.constant.TURN_TABLE_MANAGE_RECYCLER_SWIPE_EDIT
import com.maunc.toolbox.turntable.viewmodel.TurnTableDataManagerViewModel
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView

class TurnTableDataManagerActivity :
    BaseActivity<TurnTableDataManagerViewModel, ActivityTurnTableDataManagerBinding>() {

    private val turnTableDataMangerActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SOURCE_FROM_TURN_NEW_TURN_TABLE_PAGE) {
            mViewModel.queryTurnTableData()
        }
    }

    private val turnTableDataManagerAdapter by lazy {
        TurnTableDataManagerAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                if (data[pos].turnTableGroupData.isSelect) return@setOnItemClickListener
                mViewModel.selectTurnTableData(data[pos].turnTableGroupData.groupName, pos)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.turnTableDataManagerViewModel = mViewModel
        mDatabind.turnTableDataManagerAdapter = turnTableDataManagerAdapter
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.turn_table_title_manager_data_tv)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_add)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            turnTableDataMangerActivityResult.launch(obtainStartEditDataIntent())
        }
        mDatabind.turnTableDataManageNewGroupTv.clickScale {
            turnTableDataMangerActivityResult.launch(obtainStartEditDataIntent())
        }
        mDatabind.turnTableDataManageDataRecycler.setSwipeMenuCreator { leftMenu, rightMenu, pos ->
            val deleteItem = SwipeMenuItem(this)
                .setWidth(obtainDimens(R.dimen.dp_80))
                .setHeight(RecyclerView.LayoutParams.MATCH_PARENT)
                .setBackgroundColorResource(R.color.red)
                .setTextColorResource(R.color.white)
                .setTextSize(mViewModel.swipeItemTextSize)
                .setText(obtainString(R.string.turn_table_recycler_item_menu_delete_tv))
            val editItem = SwipeMenuItem(this)
                .setWidth(obtainDimens(R.dimen.dp_160))
                .setHeight(RecyclerView.LayoutParams.MATCH_PARENT)
                .setBackgroundColorResource(R.color.blue)
                .setTextColorResource(R.color.white)
                .setTextSize(mViewModel.swipeItemTextSize)
                .setText(obtainString(R.string.turn_table_recycler_item_menu_edit_tv))
            rightMenu.addMenuItem(editItem)
            rightMenu.addMenuItem(deleteItem)
        }
        mDatabind.turnTableDataManageDataRecycler.setOnItemMenuClickListener { swipeMenuBridge, pos ->
            swipeMenuBridge.closeMenu()
            val group = turnTableDataManagerAdapter.data[pos]
            val turnTableGroupData = group.turnTableGroupData
            if (swipeMenuBridge.direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                when (swipeMenuBridge.position) {
                    TURN_TABLE_MANAGE_RECYCLER_SWIPE_EDIT -> {
                        turnTableDataMangerActivityResult.launch(obtainStartEditDataIntent(group.toJson()))
                    }

                    TURN_TABLE_MANAGE_RECYCLER_SWIPE_DELETE -> {
                        mViewModel.deleteTurnTableData(turnTableGroupData.groupName, pos)
                    }
                }
            }
        }
        mDatabind.turnTableDataManageDataRecycler.layoutManager = linearLayoutManager()
        mDatabind.turnTableDataManageDataRecycler.adapter = turnTableDataManagerAdapter
        mDatabind.turnTableDataManageDataRecycler.isSwipeItemMenuEnabled = true
        mDatabind.turnTableDataManageSmartLayout.setOnTouchListener { v, event ->
            mDatabind.turnTableDataManageDataRecycler.smoothCloseMenu()
            return@setOnTouchListener true
        }
        mDatabind.turnTableDataManageSmartLayout.setOnRefreshListener {
            mDatabind.turnTableDataManageDataRecycler.smoothCloseMenu()
            mViewModel.queryTurnTableData()
        }
        mViewModel.queryTurnTableData()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        mViewModel.turnTableDataList.observe(this) {
            if (it.isEmpty()) return@observe
            mDatabind.turnTableDataManageSmartLayout.finishRefresh()
            turnTableDataManagerAdapter.setList(it)
        }
        mViewModel.deleteDataPos.observe(this) {
            if (it == -1) return@observe
            turnTableDataManagerAdapter.data.removeAt(it)
            turnTableDataManagerAdapter.notifyItemRemoved(it)
            turnTableDataManagerAdapter.notifyItemRangeRemoved(
                it, turnTableDataManagerAdapter.data.size
            )
            if (turnTableDataManagerAdapter.data.size == 0) {
                mViewModel.selectTransferData.value = -1
                mViewModel.turnTableDataIsNull.value = true
            }
            //删除选中的则恢复中转值
            if (it == mViewModel.selectTransferData.value!!) {
                mViewModel.selectTransferData.value = -1
            }
            //删除的位置小于中转值则需要减一
            if (it < mViewModel.selectTransferData.value!!) {
                mViewModel.selectTransferData.value = mViewModel.selectTransferData.value!! - 1
            }
        }
        mViewModel.selectDataPos.observe(this) {
            if (it == -1) return@observe
            // 利用中转值避免遍历所有的item刷新
            if (mViewModel.selectTransferData.value != -1) {
                turnTableDataManagerAdapter.data[mViewModel.selectTransferData.value!!]
                    .turnTableGroupData.isSelect = false
                turnTableDataManagerAdapter.notifyItemChanged(mViewModel.selectTransferData.value!!)
            }
            turnTableDataManagerAdapter.data[it].turnTableGroupData.isSelect = true
            turnTableDataManagerAdapter.notifyItemChanged(it)
            mViewModel.selectTransferData.value = it // 更新中转值
        }
    }

    /**
     * 跳转编辑页面的intent
     */
    private fun obtainStartEditDataIntent(
        dataJsonString: String = GLOBAL_NONE_STRING,
    ): Intent = obtainActivityIntentPutData(
        TurnTableEditDataActivity::class.java, mutableMapOf<String, Any>().apply {
            put(TURN_TABLE_GROUP_WITH_NAME_EXTRA, dataJsonString)
        }
    )
}