package com.maunc.toolbox.chatroom.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.adapter.ChatDataAdapter
import com.maunc.toolbox.chatroom.constant.AUDIO_PERMISSION_START_DIALOG
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_LAYOUT_UPDATE_TIME
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_RECORD_TYPE
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_TEXT_TYPE
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_DOWN
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_MOVE_CANCEL
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_UP
import com.maunc.toolbox.chatroom.data.ChatData
import com.maunc.toolbox.chatroom.viewmodel.ChatRoomViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.ext.addEditTextListener
import com.maunc.toolbox.commonbase.ext.addRecyclerViewScrollListener
import com.maunc.toolbox.commonbase.ext.animateSetWidthAndHeight
import com.maunc.toolbox.commonbase.ext.checkPermissionAvailable
import com.maunc.toolbox.commonbase.ext.checkPermissionManualRequest
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.getDimens
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.screenHeight
import com.maunc.toolbox.commonbase.ext.screenWidth
import com.maunc.toolbox.commonbase.ext.setTint
import com.maunc.toolbox.commonbase.ext.startAppSystemSettingPage
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.commonbase.utils.KeyBroadUtils
import com.maunc.toolbox.databinding.ActivityChatRoomBinding

@SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
class ChatRoomActivity : BaseActivity<ChatRoomViewModel, ActivityChatRoomBinding>() {

    companion object {
        const val ENLARGE_ANIM = 0 //执行扩大动画
        const val SHRINK_ANIM = 1 //执行缩小动画

        // 执行动画的标识(录音中的俩个布局按钮)
        const val CONTROLLER_CANCEL_VIEW = 0
        const val CONTROLLER_SURE_VIEW = 1

        //Handler消息
        const val MESSAGE_WELCOME_WHAT = 0
    }

    private val requestAudioPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (!result) {
            if (checkPermissionManualRequest(Manifest.permission.RECORD_AUDIO)) {
                CommonDialog().setTitle(
                    getString(R.string.permission_manual_request_text)
                ).setSureListener {
                    startAppSystemSettingPage()
                }.setCancelListener {
                    toast(getString(R.string.permission_manual_request_text))
                }.show(supportFragmentManager, AUDIO_PERMISSION_START_DIALOG)
            }
        }
    }

    private var userDownY = 0
    private var userDownX = 0

    private var percent12 = 12 / 100.0
    private var percent50 = 50 / 100.0

    // 是否执行过取消扩大动画
    private var executeCancelEnlargeAnim = false

    // 是否执行过取消缩小动画
    private var executeCancelShrinkAnim = false

    //是否执行过确定扩大动画
    private var executeSureEnlargeAnim = false

    //是否执行过确定缩小动画
    private var executeSureShrinkAnim = false

    private val chatDataAdapter: ChatDataAdapter by lazy {
        ChatDataAdapter()
    }

    private val chatRoomMessageHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MESSAGE_WELCOME_WHAT -> {
                    chatDataAdapter.addChatItem(
                        ChatData(
                            itemType = ChatData.CHAT_NONE_TYPE,
                            chatContent = getString(R.string.chat_room_default_content_text),
                            chatSendTime = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.chatRoomViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.tool_box_item_chat_room_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mViewModel.createVoiceRecordConfig()
        mDatabind.voiceWaveView.start()
        mDatabind.chatRoomSelectIv.setOnClickListener {
            mViewModel.chatRoomType.value =
                if (mViewModel.chatRoomType.value!! == CHAT_ROOM_TEXT_TYPE) {
                    CHAT_ROOM_RECORD_TYPE
                } else {
                    CHAT_ROOM_TEXT_TYPE
                }
        }
        mDatabind.chatRoomSendContentTv.setOnClickListener {
            val sendContent = mDatabind.chatRoomEditText.text.toString()
            if (sendContent.isEmpty()) {
                return@setOnClickListener
            }
            chatDataAdapter.addChatItem(
                ChatData(
                    itemType = ChatData.CHAT_TEXT_TYPE,
                    chatContent = sendContent,
                    chatSendTime = System.currentTimeMillis()
                )
            )
            mDatabind.chatRoomEditText.setText(GLOBAL_NONE_STRING)
        }
        KeyBroadUtils.registerKeyBoardHeightListener(this) { keyBoardHeight ->
            mViewModel.chatHandler.postDelayed({
                mDatabind.chatRoomControllerLayoutRoot.updateLayoutParams<RelativeLayout.LayoutParams> {
                    bottomMargin = keyBoardHeight
                }
                if (keyBoardHeight > 0) {
                    mDatabind.chatRoomRecycler.scrollToPosition(
                        chatDataAdapter.itemCount - 1
                    )
                }
            }, CHAT_ROOM_LAYOUT_UPDATE_TIME)
        }
        mDatabind.chatRoomRecordStartButton.setOnTouchListener { v, event ->
            if (!checkPermissionAvailable(Manifest.permission.RECORD_AUDIO)) {
                requestAudioPermissionResult.launch(Manifest.permission.RECORD_AUDIO)
                return@setOnTouchListener false
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mViewModel.launchVibrator()
                    userDownY = event.rawY.toInt()
                    userDownX = event.rawX.toInt()
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_DOWN
                    mViewModel.startRecordVoice()
                    mViewModel.isWriteWavHeader.postValue(true)
                }

                MotionEvent.ACTION_UP -> {
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_UP
                    startScaleAnim(SHRINK_ANIM, CONTROLLER_CANCEL_VIEW)
                    startScaleAnim(SHRINK_ANIM, CONTROLLER_SURE_VIEW)
                    mViewModel.stopRecordVoice()
                }

                MotionEvent.ACTION_MOVE -> {
                    // 未滑动到可以操作按钮范围
                    if (userDownY - event.rawY.toInt() < (screenHeight() * (percent12))) {
                        executeCancelEnlargeAnim = false
                        executeSureEnlargeAnim = false
                        if (!executeCancelShrinkAnim) {
                            executeCancelShrinkAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_CANCEL_VIEW)
                            mViewModel.isWriteWavHeader.postValue(true)
                        }
                        if (!executeSureShrinkAnim) {
                            executeSureShrinkAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_SURE_VIEW)
                        }
                        return@setOnTouchListener true
                    }
                    // 选中sure
                    if (event.rawX.toInt() > (screenWidth() * percent50)) {
                        if (!executeSureEnlargeAnim) {
                            executeSureEnlargeAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
                            startScaleAnim(ENLARGE_ANIM, CONTROLLER_SURE_VIEW)
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_CANCEL_VIEW)
                            mViewModel.isWriteWavHeader.postValue(true)
                        }
                    } else {
                        //选中cancel
                        if (!executeCancelEnlargeAnim) {
                            executeCancelEnlargeAnim = true
                            mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_MOVE_CANCEL
                            startScaleAnim(ENLARGE_ANIM, CONTROLLER_CANCEL_VIEW)
                            startScaleAnim(SHRINK_ANIM, CONTROLLER_SURE_VIEW)
                            mViewModel.isWriteWavHeader.postValue(false)
                        }
                    }
                }
            }
            return@setOnTouchListener true
        }
        mDatabind.chatRoomEditText.addEditTextListener(afterTextChanged = {
            mViewModel.editLength.value = it.length
        })
        mDatabind.chatRoomRecycler.layoutManager = linearLayoutManager()
        mDatabind.chatRoomRecycler.adapter = chatDataAdapter
        // 发送初始化消息
        chatRoomMessageHandler.sendEmptyMessageDelayed(MESSAGE_WELCOME_WHAT, 500L)
        mDatabind.chatRoomRecycler.addRecyclerViewScrollListener(onScrollStateChanged = { _, newState ->
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                // 只有触摸态才会收起布局
                mViewModel.hideSoftInputKeyBoard(mDatabind.chatRoomEditText)
            }
        })
        mDatabind.chatRoomSmartLayout.setOnRefreshListener {
            mViewModel.chatHandler.postDelayed({
                mDatabind.chatRoomSmartLayout.finishRefresh()
            }, 100)
        }
    }

    override fun createObserver() {
        mViewModel.chatRoomType.observe(this) {
            when (it) {
                CHAT_ROOM_RECORD_TYPE -> {
                    mViewModel.hideSoftInputKeyBoard(mDatabind.chatRoomEditText)
                }

                CHAT_ROOM_TEXT_TYPE -> {
                    mViewModel.showSoftInputKeyBoard(mDatabind.chatRoomEditText)
                }
            }
        }
    }

    private fun startScaleAnim(
        animType: Int, viewType: Int,
        action: () -> Unit = {
            when (viewType) {
                CONTROLLER_SURE_VIEW -> {
                    executeSureShrinkAnim = false
                    executeSureEnlargeAnim = false
                }

                CONTROLLER_CANCEL_VIEW -> {
                    executeCancelShrinkAnim = false
                    executeCancelEnlargeAnim = false
                }
            }
        },
    ) {
        when (viewType) {
            CONTROLLER_SURE_VIEW -> {
                if (animType == ENLARGE_ANIM) {
                    mDatabind.voiceRecordUpSureBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_120),
                        targetHeight = getDimens(R.dimen.dp_120)
                    ) {
                        action()
                        mDatabind.voiceRecordUpSureBg.setBackgroundResource(
                            R.drawable.bg_record_controller_sure_oval
                        )
                        mDatabind.voiceRecordUpSureIcon.setTint(R.color.white)
                    }
                }
                if (animType == SHRINK_ANIM) {
                    mDatabind.voiceRecordUpSureBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_100),
                        targetHeight = getDimens(R.dimen.dp_100)
                    ) {
                        action()
                        mDatabind.voiceRecordUpSureBg.setBackgroundResource(
                            R.drawable.bg_record_controller_none_oval
                        )
                        mDatabind.voiceRecordUpSureIcon.setTint(R.color.black)
                    }
                }
            }

            CONTROLLER_CANCEL_VIEW -> {
                if (animType == ENLARGE_ANIM) {
                    mDatabind.voiceRecordUpCancelBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_120),
                        targetHeight = getDimens(R.dimen.dp_120)
                    ) {
                        action()
                        mDatabind.voiceRecordUpCancelBg.setBackgroundResource(
                            R.drawable.bg_record_controller_cancel_oval
                        )
                        mDatabind.voiceRecordUpCancelIcon.setTint(R.color.white)
                    }
                }
                if (animType == SHRINK_ANIM) {
                    mDatabind.voiceRecordUpCancelBg.animateSetWidthAndHeight(
                        targetWidth = getDimens(R.dimen.dp_100),
                        targetHeight = getDimens(R.dimen.dp_100)
                    ) {
                        action()
                        mDatabind.voiceRecordUpCancelBg.setBackgroundResource(
                            R.drawable.bg_record_controller_none_oval
                        )
                        mDatabind.voiceRecordUpCancelIcon.setTint(R.color.black)
                    }
                }
            }
        }
    }
}
