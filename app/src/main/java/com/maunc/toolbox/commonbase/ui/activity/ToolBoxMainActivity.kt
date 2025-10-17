package com.maunc.toolbox.commonbase.ui.activity

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.ui.activity.ChatRoomActivity
import com.maunc.toolbox.chronograph.ui.ChronographMainActivity
import com.maunc.toolbox.commonbase.adapter.ToolBoxManagerAdapter
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.addCustomizeItemDecoration
import com.maunc.toolbox.commonbase.ext.gridLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.utils.obtainMMKV
import com.maunc.toolbox.commonbase.viewmodel.ToolBoxMainViewModel
import com.maunc.toolbox.databinding.ActivityToolBoxMainBinding
import com.maunc.toolbox.randomname.ui.activity.RandomNameWelcomeActivity
import com.maunc.toolbox.signaturecanvas.ui.activity.SignatureCanvasMainActivity

class ToolBoxMainActivity : BaseActivity<ToolBoxMainViewModel, ActivityToolBoxMainBinding>() {

    private companion object {
        const val ADAPTER_SPAN_COUNT = 2
    }

    private val toolBoxManagerAdapter by lazy {
        ToolBoxManagerAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val itemData = getItem(pos)
                when (itemData.itemTitle) {
                    obtainString(R.string.tool_box_item_chronograph_text) -> {
                        startTargetActivity(ChronographMainActivity::class.java)
                    }

                    obtainString(R.string.tool_box_item_random_name_text) -> {
                        startTargetActivity(RandomNameWelcomeActivity::class.java)
                    }

                    obtainString(R.string.tool_box_item_chat_room_text) -> {
                        startTargetActivity(ChatRoomActivity::class.java)
                    }

                    obtainString(R.string.tool_box_item_signature_canvas_text) -> {
                        startTargetActivity(SignatureCanvasMainActivity::class.java)
                    }
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 初始化mmkv所有默认配置
        obtainMMKV.init()
        mDatabind.toolBoxMainRecycler.layoutManager = gridLayoutManager(ADAPTER_SPAN_COUNT)
        mDatabind.toolBoxMainRecycler.addCustomizeItemDecoration()
        mDatabind.toolBoxMainRecycler.adapter = toolBoxManagerAdapter
        toolBoxManagerAdapter.setList(mViewModel.initRecyclerData())
    }

    override fun createObserver() {

    }
}
