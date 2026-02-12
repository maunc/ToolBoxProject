package com.maunc.toolbox.chatroom.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.adapter.ChatDataAdapter
import com.maunc.toolbox.chatroom.constant.AUDIO_PERMISSION_START_DIALOG
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_RECORD_TYPE
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_TEXT_TYPE
import com.maunc.toolbox.chatroom.constant.FULL_SCREEN_IMAGE_DATA_EXTRA
import com.maunc.toolbox.chatroom.constant.FULL_SCREEN_IMAGE_POS_EXTRA
import com.maunc.toolbox.chatroom.constant.PERCENT_FIFTY
import com.maunc.toolbox.chatroom.constant.PERCENT_TWELVE
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_DOWN
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_MOVE_CANCEL
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_UP
import com.maunc.toolbox.chatroom.constant.SEND_IMAGE_MAX_NUM
import com.maunc.toolbox.chatroom.constant.TEXT_CONTENT_DATA_EXTRA
import com.maunc.toolbox.chatroom.constant.TEXT_SEND_TIME_DATA_EXTRA
import com.maunc.toolbox.chatroom.data.ChatImageData
import com.maunc.toolbox.chatroom.data.ChatRecordData
import com.maunc.toolbox.chatroom.viewmodel.ChatRoomViewModel
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.ARRAY_INDEX_ZERO
import com.maunc.toolbox.commonbase.constant.FIVE_DELAY_MILLIS
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.commonbase.constant.ONE_DELAY_MILLIS
import com.maunc.toolbox.commonbase.constant.ONE_DELAY_S
import com.maunc.toolbox.commonbase.ext.addEditTextListener
import com.maunc.toolbox.commonbase.ext.addRecyclerViewScrollListener
import com.maunc.toolbox.commonbase.ext.animateSetWidthAndHeight
import com.maunc.toolbox.commonbase.ext.checkPermissionAvailable
import com.maunc.toolbox.commonbase.ext.checkPermissionManualRequest
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.hideSoftInputKeyBoard
import com.maunc.toolbox.commonbase.ext.launchVibrator
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainDimens
import com.maunc.toolbox.commonbase.ext.obtainGlideEngin
import com.maunc.toolbox.commonbase.ext.obtainScreenHeight
import com.maunc.toolbox.commonbase.ext.obtainScreenWidth
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.setTint
import com.maunc.toolbox.commonbase.ext.showSoftInputKeyBoard
import com.maunc.toolbox.commonbase.ext.startActivityWithData
import com.maunc.toolbox.commonbase.ext.startAppSystemSettingPage
import com.maunc.toolbox.commonbase.ext.toJson
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.commonbase.ui.dialog.CommonDialog
import com.maunc.toolbox.commonbase.utils.KeyBroadUtils
import com.maunc.toolbox.databinding.ActivityChatRoomBinding
import java.io.File

@SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
class ChatRoomActivity : BaseActivity<ChatRoomViewModel, ActivityChatRoomBinding>(),
    ChatDataAdapter.ChatRoomOnUserItemListener,
    ChatDataAdapter.ChatRoomOnBotItemListener {

    companion object {
        const val ENLARGE_ANIM = 0 //执行扩大动画
        const val SHRINK_ANIM = 1 //执行缩小动画

        // 执行动画的标识(录音中的俩个布局按钮)
        const val CONTROLLER_CANCEL_VIEW = 0
        const val CONTROLLER_SURE_VIEW = 1

        //Handler消息
        const val MESSAGE_FIRST_WELCOME_WHAT = 0//初次打招呼消息
        const val MESSAGE_AUDIO_CURRENT_TIME_WHAT = 1//录音中时间消息
    }

    private val requestRecordAudioPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (!result) {
            if (checkPermissionManualRequest(Manifest.permission.RECORD_AUDIO)) {
                showPermissionForeverNotTips()
            }
        }
    }

    private val requestCameraPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (!result) {
            if (checkPermissionManualRequest(Manifest.permission.CAMERA)) {
                showPermissionForeverNotTips()
            }
        }
    }

    private fun showPermissionForeverNotTips() {
        restoreOriginalStateView()
        CommonDialog().setTitle(
            obtainString(R.string.permission_manual_request_text)
        ).setSureListener {
            startAppSystemSettingPage()
        }.setCancelListener {
            toast(getString(R.string.permission_manual_request_text))
        }.show(supportFragmentManager, AUDIO_PERMISSION_START_DIALOG)
    }

    private var userDownY = 0
    private var userDownX = 0

    private var executeCancelEnlargeAnim = false//是否执行过取消扩大动画
    private var executeCancelShrinkAnim = false //是否执行过取消缩小动画
    private var executeSureEnlargeAnim = false //是否执行过确定扩大动画
    private var executeSureShrinkAnim = false //是否执行过确定缩小动画

    private val chatDataAdapter: ChatDataAdapter by lazy {
        ChatDataAdapter()
    }

    private val chatRoomHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MESSAGE_FIRST_WELCOME_WHAT -> {
                    chatDataAdapter.addChatBotTextItem(obtainString(R.string.chat_room_default_one_content_text))
                }

                MESSAGE_AUDIO_CURRENT_TIME_WHAT -> {
                    sendAudioTimeMsg()
                }
            }
        }
    }

    private fun sendFirstWelcomeMsg() {
        chatDataAdapter.addChatNoneItem(getString(R.string.chat_room_welcome_text))
        chatRoomHandler.sendEmptyMessageDelayed(
            MESSAGE_FIRST_WELCOME_WHAT,
            FIVE_DELAY_MILLIS
        )
    }

    private fun sendAudioTimeMsg() {
        mViewModel.currentAudioTime.value = mViewModel.currentAudioTime.value!! + 1
        chatRoomHandler.sendEmptyMessageDelayed(MESSAGE_AUDIO_CURRENT_TIME_WHAT, ONE_DELAY_S)
    }

    private fun clearAudioTimeMsg() {
        mViewModel.currentAudioTime.value = 0
        chatRoomHandler.removeMessages(MESSAGE_AUDIO_CURRENT_TIME_WHAT)
    }

    override fun onBackPressCallBack() {
        if (mViewModel.controllerButtonSelect.value!!) {
            restoreOriginalStateView()
            return
        }
        finishCurrentActivity()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.chatRoomViewModel = mViewModel
        mDatabind.chatDataAdapter = chatDataAdapter
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.tool_box_item_chat_room_text)
        mViewModel.createVoiceRecordConfig()
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.chatRoomSelectIv.setOnClickListener {
            if (mViewModel.chatRoomType.value!! == CHAT_ROOM_TEXT_TYPE) {
                mViewModel.chatRoomType.value = CHAT_ROOM_RECORD_TYPE
            } else {
                mViewModel.chatRoomType.value = CHAT_ROOM_TEXT_TYPE
            }
            mViewModel.cleaMoreLayoutHeight.value =
                mViewModel.chatRoomType.value!! == CHAT_ROOM_RECORD_TYPE
        }
        /*val frameAnimHelper = FrameAnimHelper(
            mDatabind.chatRoomEmIv,
            R.array.array_voice_play_left_anim,
            R.drawable.icon_voice_left_third,
            200,
            true
        )*/
        mDatabind.chatRoomSendContentTv.setOnClickListener {
            val sendContent = mDatabind.chatRoomEditText.text.toString()
            if (sendContent.isEmpty()) {
                return@setOnClickListener
            }
            if (mViewModel.chatRoomType.value!! != CHAT_ROOM_TEXT_TYPE) {
                return@setOnClickListener
            }
            chatDataAdapter.addChatTextItem(sendContent)
            clearChatRoomEditContent()
        }
        KeyBroadUtils.registerKeyBoardHeightListener(this) {
            mViewModel.softKeyBroadHeight.postValue(it)
            if (!mViewModel.controllerButtonSelect.value!!) {
                mViewModel.cleaMoreLayoutHeight.value = it <= 0
            }
        }
        //录音按钮触摸
        mDatabind.chatRoomRecordStartButton.setOnTouchListener { v, event ->
            if (!checkPermissionAvailable(Manifest.permission.RECORD_AUDIO)) {
                requestRecordAudioPermissionResult.launch(Manifest.permission.RECORD_AUDIO)
                return@setOnTouchListener false
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    launchVibrator()
                    mViewModel.controllerButtonSelect.value = false
                    userDownY = event.rawY.toInt()
                    userDownX = event.rawX.toInt()
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_DOWN
                    mViewModel.startRecordVoice()
                    sendAudioTimeMsg()
                    mViewModel.isWriteWavHeader.postValue(true)
                }

                MotionEvent.ACTION_UP -> {
                    mViewModel.recordViewStatus.value = RECORD_VIEW_STATUS_UP
                    startScaleAnim(SHRINK_ANIM, CONTROLLER_CANCEL_VIEW)
                    startScaleAnim(SHRINK_ANIM, CONTROLLER_SURE_VIEW)
                    mViewModel.stopRecordVoice()
                    if (mViewModel.isWriteWavHeader.value!!) {
                        chatDataAdapter.addChatAudioItem(
                            mViewModel.audioFilePath,
                            mViewModel.currentAudioTime.value!!
                        )
                    }
                    clearAudioTimeMsg()
                }

                MotionEvent.ACTION_MOVE -> {
                    // 未滑动到可以操作按钮范围
                    if (userDownY - event.rawY.toInt() < (obtainScreenHeight() * (PERCENT_TWELVE))) {
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
                    if (event.rawX.toInt() > (obtainScreenWidth() * PERCENT_FIFTY)) {
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
        mDatabind.chatRoomEditText.setOnClickListener {
            mViewModel.cleaMoreLayoutHeight.value = false
            mViewModel.controllerButtonSelect.value = false
        }
        mDatabind.chatRoomEditText.addEditTextListener(afterTextChanged = { editString ->
            mViewModel.editContentString.value = editString
        })
        mDatabind.chatRoomRecycler.layoutManager = linearLayoutManager()
        mDatabind.chatRoomRecycler.adapter = chatDataAdapter
        chatDataAdapter.setClickUserItemListener(this@ChatRoomActivity)
        chatDataAdapter.setClickBotItemListener(this@ChatRoomActivity)
        // 发送初始化消息
        sendFirstWelcomeMsg()
        mDatabind.chatRoomRecycler.addRecyclerViewScrollListener(onScrollStateChanged = { _, newState ->
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                // 只有触摸态才会收起布局
                restoreOriginalStateView()
            }
        })
        mDatabind.chatRoomSmartLayout.setOnRefreshListener {
            mDatabind.chatRoomSmartLayout.postDelayed({
                mDatabind.chatRoomSmartLayout.finishRefresh()
            }, ONE_DELAY_MILLIS)
        }
        mDatabind.chatRoomPicIcon.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(obtainGlideEngin)
                .setMaxSelectNum(SEND_IMAGE_MAX_NUM)
                .forResult(mViewModel.onPicSelectResultCallbackListener { resultList ->
                    restoreOriginalStateView()
                    resultList?.forEach { resultItem ->
                        resultItem.path?.let {
                            chatDataAdapter.addChatImageFileItem(it)
                        }
                    }
                })
        }
        mDatabind.chatRoomPhotoIcon.setOnClickListener {
            if (!checkPermissionAvailable(Manifest.permission.CAMERA)) {
                requestCameraPermissionResult.launch(Manifest.permission.CAMERA)
                return@setOnClickListener
            }
            PictureSelector.create(this)
                .openCamera(SelectMimeType.ofImage())
                .forResult(mViewModel.onPicSelectResultCallbackListener { resultList ->
                    restoreOriginalStateView()
                    resultList?.get(ARRAY_INDEX_ZERO)?.path?.let {
                        chatDataAdapter.addChatImageFileItem(it)
                    }
                })
        }
        mDatabind.chatRoomMoreIcon.setOnClickListener {
            mViewModel.refreshLayout.value = !mViewModel.refreshLayout.value!!
            mViewModel.controllerButtonSelect.value = true
            mViewModel.cleaMoreLayoutHeight.value = false
            if (mViewModel.refreshLayout.value!!) {
                showSoftInputKeyBoard(mDatabind.chatRoomEditText)
            } else {
                hideSoftInputKeyBoard(mDatabind.chatRoomEditText)
            }
        }
        mDatabind.chatRoomEmIcon.setOnClickListener {
            mViewModel.refreshLayout.value = !mViewModel.refreshLayout.value!!
            mViewModel.controllerButtonSelect.value = true
            mViewModel.cleaMoreLayoutHeight.value = false
            if (mViewModel.refreshLayout.value!!) {
                showSoftInputKeyBoard(mDatabind.chatRoomEditText)
            } else {
                hideSoftInputKeyBoard(mDatabind.chatRoomEditText)
            }
        }
    }

    // 清空输入框内容
    private fun clearChatRoomEditContent() {
        mDatabind.chatRoomEditText.setText(GLOBAL_NONE_STRING)
        mViewModel.editContentString.value = GLOBAL_NONE_STRING
    }

    override fun createObserver() {
        mViewModel.chatRoomType.observe(this) {
            when (it) {
                CHAT_ROOM_RECORD_TYPE -> hideSoftInputKeyBoard(mDatabind.chatRoomEditText)
                CHAT_ROOM_TEXT_TYPE -> showSoftInputKeyBoard(mDatabind.chatRoomEditText)
            }
        }
    }

    /**
     * 收起键盘，并收起更多功能Layout
     */
    private fun restoreOriginalStateView() {
        hideSoftInputKeyBoard(mDatabind.chatRoomEditText)
        mViewModel.cleaMoreLayoutHeight.value = true
        mViewModel.controllerButtonSelect.value = false
    }

    override fun onClickUserTextItem() {

    }

    override fun onDoubleTapUserTextItem(
        content: String,
        sendTime: Long,
    ) = startShowTextPage(content, sendTime)

    override fun onClickUserImageItem(
        chatImageData: ChatImageData,
        chatImageList: MutableList<ChatImageData>,
        itemViewPosition: Int,
        clickImagePosition: Int,
    ) = startActivityWithData(
        ChatRoomShowPicActivity::class.java,
        mutableMapOf<String, Any>().apply {
            put(FULL_SCREEN_IMAGE_DATA_EXTRA, chatImageList.toJson())
            put(FULL_SCREEN_IMAGE_POS_EXTRA, clickImagePosition)
        }
    )

    override fun onClickUserAudioItem(
        chatRecordData: ChatRecordData,
        itemViewPosition: Int,
    ) {
        chatRecordData.filePath?.let { File(it) }?.let { audioFile ->
            mViewModel.playerWavFilePath(audioFile)
        }
    }

    override fun onDoubleTapBotTextItem(
        content: String,
        sendTime: Long,
    ) = startShowTextPage(content, sendTime)

    private fun startShowTextPage(content: String, sendTime: Long) {
        startActivityWithData(
            ChatRoomShowTextActivity::class.java,
            mutableMapOf<String, Any>().apply {
                put(TEXT_CONTENT_DATA_EXTRA, content)
                put(TEXT_SEND_TIME_DATA_EXTRA, sendTime)
            }
        )
    }

    /**
     * 语音状态动画
     */
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
                        targetWidth = obtainDimens(R.dimen.dp_120),
                        targetHeight = obtainDimens(R.dimen.dp_120)
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
                        targetWidth = obtainDimens(R.dimen.dp_100),
                        targetHeight = obtainDimens(R.dimen.dp_100)
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
                        targetWidth = obtainDimens(R.dimen.dp_120),
                        targetHeight = obtainDimens(R.dimen.dp_120)
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
                        targetWidth = obtainDimens(R.dimen.dp_100),
                        targetHeight = obtainDimens(R.dimen.dp_100)
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
