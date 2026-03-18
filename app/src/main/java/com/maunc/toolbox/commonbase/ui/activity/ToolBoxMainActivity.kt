package com.maunc.toolbox.commonbase.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import com.maunc.toolbox.R
import com.maunc.toolbox.appViewModel
import com.maunc.toolbox.chatroom.ui.activity.ChatRoomActivity
import com.maunc.toolbox.chronograph.ui.ChronographMainActivity
import com.maunc.toolbox.commonbase.adapter.ToolBoxManagerAdapter
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.data.ToolBoxItemData
import com.maunc.toolbox.commonbase.ext.addCustomizeItemDecoration
import com.maunc.toolbox.commonbase.ext.addItemTouchHelper
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainDrawable
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.setScale
import com.maunc.toolbox.commonbase.ext.startTargetActivity
import com.maunc.toolbox.commonbase.utils.checkFilePermission
import com.maunc.toolbox.commonbase.viewmodel.ToolBoxMainViewModel
import com.maunc.toolbox.databinding.ActivityToolBoxMainBinding
import com.maunc.toolbox.devicemsg.ui.DeviceMessageActivity
import com.maunc.toolbox.ffmpeg.ui.FFmpegMainActivity
import com.maunc.toolbox.ftp.ui.activity.FtpMainActivity
import com.maunc.toolbox.localfile.ui.LocalFileMainActivity
import com.maunc.toolbox.pushbox.ui.activity.PushBoxMainActivity
import com.maunc.toolbox.randomname.ui.activity.RandomNameMainActivity
import com.maunc.toolbox.signaturecanvas.ui.SignatureCanvasMainActivity
import com.maunc.toolbox.turntable.ui.TurnTableMainActivity
import java.util.Collections

class ToolBoxMainActivity : BaseActivity<ToolBoxMainViewModel, ActivityToolBoxMainBinding>() {

    private val toolBoxManagerAdapter by lazy {
        ToolBoxManagerAdapter().apply {
            setOnItemClickListener { adapter, view, pos ->
                val itemData = getItem(pos)
                when (itemData.itemType) {
                    ToolBoxItemData.TOOL_BOX_ITEM_CHRONOGRAPH ->
                        startTargetActivity(ChronographMainActivity::class.java)

                    ToolBoxItemData.TOOL_BOX_ITEM_RANDOM_NAME ->
                        startTargetActivity(RandomNameMainActivity::class.java)

                    ToolBoxItemData.TOOL_BOX_ITEM_CHAT_ROOM ->
                        startTargetActivity(ChatRoomActivity::class.java)

                    ToolBoxItemData.TOOL_BOX_ITEM_SIGNATURE_CANVAS -> {
                        startTargetActivity(SignatureCanvasMainActivity::class.java)
                    }

                    ToolBoxItemData.TOOL_BOX_ITEM_TURN_TABLE ->
                        startTargetActivity(TurnTableMainActivity::class.java)

                    ToolBoxItemData.TOOL_BOX_ITEM_FFMPEG -> if (checkFilePermission()) {
                        startTargetActivity(FFmpegMainActivity::class.java)
                    }

                    ToolBoxItemData.TOOL_BOX_ITEM_PUSH_BOX ->
                        startTargetActivity(PushBoxMainActivity::class.java)

                    ToolBoxItemData.TOOL_BOX_ITEM_FTP -> if (checkFilePermission()) {
                        startTargetActivity(FtpMainActivity::class.java)
                    }

                    ToolBoxItemData.TOOL_BOX_ITEM_DEVICE_MSG ->
                        startTargetActivity(DeviceMessageActivity::class.java)

                    ToolBoxItemData.TOOL_BOX_ITEM_LOCAL_FILE -> if (checkFilePermission()) {
                        startTargetActivity(LocalFileMainActivity::class.java)
                    }
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        appViewModel.initMMKV()
        mDatabind.commonToolBar.commonToolBarBackButton.setImageResource(R.drawable.icon_meun)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_setting)
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.app_name)
        mDatabind.toolBoxMainRecycler.layoutManager = linearLayoutManager()
        mDatabind.toolBoxMainRecycler.addCustomizeItemDecoration()
        mDatabind.toolBoxMainRecycler.adapter = toolBoxManagerAdapter
        mViewModel.initToolBoxItemList()
        mDatabind.toolBoxMainRecycler.addItemTouchHelper(
            onMove = { fromPosition, toPosition ->
                val currentList = toolBoxManagerAdapter.data
                if (fromPosition < toPosition) { // 从下往上拖，依次交换
                    for (i in fromPosition until toPosition) {
                        Collections.swap(currentList, i, i + 1)
                    }
                } else { // 从上往下拖，依次交换
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(currentList, i, i - 1)
                    }
                }
                toolBoxManagerAdapter.notifyItemMoved(fromPosition, toPosition)
                return@addItemTouchHelper true
            },
            onSelectedChanged = { viewHolder, actionState ->
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder?.itemView?.setScale(1.1f, 1.1f)
                    viewHolder?.itemView?.background =
                        obtainDrawable(R.drawable.stroke_black_bg_gray_radius_24)
                }
            },
            onClearView = { recyclerView, viewHolder ->
                if (!recyclerView.isComputingLayout) {
                    mViewModel.updateToolBoxList(toolBoxManagerAdapter.data.mapIndexed { index, data ->
                        data.copy(itemSort = index)
                    }.toMutableList())
                    viewHolder.itemView.setScale(1.0f, 1.0f)
                    viewHolder.itemView.background =
                        obtainDrawable(R.drawable.bg_item_tool_box_selector)
                }
            }
        )
    }

    override fun createObserver() {
        mViewModel.toolBoxListLiveData.observe(this) {
            toolBoxManagerAdapter.setList(it)
        }
    }
}