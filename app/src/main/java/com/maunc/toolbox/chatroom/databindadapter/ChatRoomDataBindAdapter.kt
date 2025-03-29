package com.maunc.toolbox.chatroom.databindadapter

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maunc.toolbox.R
import com.maunc.toolbox.chatroom.adapter.ChatDataAdapter
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_LAYOUT_UPDATE_TIME
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_RECORD_TYPE
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_TEXT_TYPE
import com.maunc.toolbox.chatroom.constant.EDIT_FIVE_LINE
import com.maunc.toolbox.chatroom.constant.EDIT_FOUR_LINE
import com.maunc.toolbox.chatroom.constant.EDIT_NONE_LINE
import com.maunc.toolbox.chatroom.constant.EDIT_ONE_LINE
import com.maunc.toolbox.chatroom.constant.EDIT_THREE_LINE
import com.maunc.toolbox.chatroom.constant.EDIT_TWO_LINE
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_DOWN
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_MOVE_CANCEL
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_UP
import com.maunc.toolbox.commonbase.ext.animateSetHeight
import com.maunc.toolbox.commonbase.ext.animateToAlpha
import com.maunc.toolbox.commonbase.ext.getColor
import com.maunc.toolbox.commonbase.ext.getDrawable
import com.maunc.toolbox.commonbase.ext.getString
import com.maunc.toolbox.commonbase.ext.gone
import com.maunc.toolbox.commonbase.ext.setHeight
import com.maunc.toolbox.commonbase.ext.visible
import com.us.mauncview.VoiceWaveView

object ChatRoomDataBindAdapter {

    /**
     * 录制语音按钮的样式文本
     */
    @JvmStatic
    @BindingAdapter(value = ["handleRecordButton"], requireAll = false)
    fun handleRecordButton(textView: TextView, recordStatus: Int) {
        when (recordStatus) {
            RECORD_VIEW_STATUS_DOWN, RECORD_VIEW_STATUS_MOVE_CANCEL_DONE -> {
                textView.text = getString(R.string.voice_record_up_tips)
                textView.setBackgroundResource(R.drawable.bg_white_50_radius_12)
            }

            RECORD_VIEW_STATUS_UP -> {
                textView.text = getString(R.string.voice_record_down_tips)
                textView.setBackgroundResource(R.drawable.bg_white_radius_12)
            }

            RECORD_VIEW_STATUS_MOVE_CANCEL -> {
                textView.text = getString(R.string.voice_record_up_cancel_tips)
            }
        }
    }

    /**
     * 波浪View的颜色
     */
    @JvmStatic
    @BindingAdapter(value = ["handleVoiceWaveViewColor"], requireAll = false)
    fun handleVoiceWaveViewColor(voiceWaveView: VoiceWaveView, recordStatus: Int) {
        voiceWaveView.apply {
            when (recordStatus) {
                RECORD_VIEW_STATUS_UP, RECORD_VIEW_STATUS_MOVE_CANCEL_DONE -> {
                    lineColor = getColor(R.color.blue)
                }

                RECORD_VIEW_STATUS_MOVE_CANCEL -> {
                    lineColor = getColor(R.color.red)
                }
            }
        }
    }

    /**
     * 波浪动画是否启动
     */
    @JvmStatic
    @BindingAdapter(value = ["handleVoiceWaveViewAnim"], requireAll = false)
    fun handleVoiceWaveViewAnim(view: VoiceWaveView, recordStatus: Int) {
        when (recordStatus) {
            RECORD_VIEW_STATUS_DOWN -> view.start()
            RECORD_VIEW_STATUS_UP -> view.stop()
            RECORD_VIEW_STATUS_MOVE_CANCEL -> {}
            RECORD_VIEW_STATUS_MOVE_CANCEL_DONE -> {}
            else -> view.stop()
        }
    }

    /**
     * 录音布局的可见性
     */
    @JvmStatic
    @BindingAdapter(value = ["handleRecordLayoutVisible"], requireAll = false)
    fun handleRecordLayoutVisible(view: View, recordStatus: Int) {
        when (recordStatus) {
            RECORD_VIEW_STATUS_DOWN -> view.animateToAlpha(
                startAlpha = 0f,
                endAlpha = 1f,
                time = 200
            )

            RECORD_VIEW_STATUS_UP -> view.animateToAlpha(
                startAlpha = 1f,
                endAlpha = 0f,
                time = 100
            )

            RECORD_VIEW_STATUS_MOVE_CANCEL -> {}
            RECORD_VIEW_STATUS_MOVE_CANCEL_DONE -> {}
            else -> view.gone()
        }
    }

    /**
     * 语音模式布局与输入框布局切换
     */
    @JvmStatic
    @BindingAdapter(value = ["handleEditWithChatType"], requireAll = false)
    fun handleEditWithChatRoomType(view: EditText, type: Int) {
        when (type) {
            CHAT_ROOM_TEXT_TYPE -> view.visible()
            CHAT_ROOM_RECORD_TYPE -> view.gone()
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleRecordButtonWithChatType"], requireAll = false)
    fun handleRecordButtonWithChatType(view: TextView, type: Int) {
        when (type) {
            CHAT_ROOM_TEXT_TYPE -> view.gone()
            CHAT_ROOM_RECORD_TYPE -> view.visible()
        }
    }

    /**
     * 切换模式按钮样式
     */
    @JvmStatic
    @BindingAdapter(value = ["handleChatRoomSelectIv"], requireAll = false)
    fun handleChatRoomSelectIv(view: ImageView, type: Int) {
        when (type) {
            CHAT_ROOM_TEXT_TYPE -> view.setImageResource(R.drawable.icon_chat_room_check_record)
            CHAT_ROOM_RECORD_TYPE -> view.setImageResource(R.drawable.icon_chat_room_check_text)
        }
    }

    /**
     * 发送按钮样式
     */
    @JvmStatic
    @BindingAdapter(value = ["handleSendButtonEnable"], requireAll = false)
    fun handleSendButtonEnable(textView: TextView, editString: String) {
        textView.isEnabled = editString.isNotEmpty()
        if (!textView.isEnabled) {
            textView.background = getDrawable(R.drawable.chat_room_send_button_bg_not_enable)
            textView.setTextColor(getColor(R.color.white_75))
        } else {
            textView.background = getDrawable(R.drawable.chat_room_send_button_bg_enable)
            textView.setTextColor(getColor(R.color.white))
        }
    }

    /**
     * 输入框大小适配
     */
    @JvmStatic
    @BindingAdapter(value = ["handleEditContent", "handleEditMaxWidth"], requireAll = true)
    fun handlerEditTextHeight(editText: EditText, editString: String, editTextMaxWidth: Int) {
        val textPaint = editText.paint
        val oneCharChineseWidth = textPaint.measureText("A")
        var exceedNum = 0
        var currentWidth = 0
        for (i in editString.indices) {
            val measureText = textPaint.measureText(editString[i].toString()).toInt()
            currentWidth += measureText
            if (currentWidth > editTextMaxWidth - oneCharChineseWidth) {
                exceedNum++
                currentWidth = 0
            }
        }
        when (exceedNum) {
            0 -> editText.animateSetHeight(EDIT_NONE_LINE)
            1 -> editText.animateSetHeight(EDIT_ONE_LINE)
            2 -> editText.animateSetHeight(EDIT_TWO_LINE)
            3 -> editText.animateSetHeight(EDIT_THREE_LINE)
            4 -> editText.animateSetHeight(EDIT_FOUR_LINE)
            5 -> editText.animateSetHeight(EDIT_FIVE_LINE)
        }
    }

    /**
     * 软键盘弹出处理列表位置
     */
    @JvmStatic
    @BindingAdapter(value = ["handleRecyclerLocation", "handleRecyclerAdapter"], requireAll = false)
    fun handleShowKeyBroadWithRecycler(
        view: View,
        handleHeight: Int,
        recyclerAdapter: ChatDataAdapter,
    ) {
        //传入的刷新布局为包含关系所以能findRecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_room_recycler)
        if (handleHeight > 0) {
            view.postDelayed({
                recyclerView.scrollToPosition(/*列表的最后一条数据的pos*/recyclerAdapter.itemCount - 1)
            }, CHAT_ROOM_LAYOUT_UPDATE_TIME)
        }
    }

    /**
     * 控制台所在位置处理
     */
    @JvmStatic
    @BindingAdapter(
        value = [
            "handleControllerLayoutParams",
            "handleRefreshControllerLayout"
        ], requireAll = true
    )
    fun handleControllerLayoutParams(
        view: View,
        handleHeight: Int,
        refreshLayout: Boolean,
    ) {
        if (!refreshLayout) {
            return
        }
        view.postDelayed({
            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = handleHeight
            }
        }, CHAT_ROOM_LAYOUT_UPDATE_TIME)
    }

    /**
     * 处理软键盘下面的布局高度
     */
    @JvmStatic
    @BindingAdapter(value = ["handleMoreLayoutHeight", "handleMoreClearHeight"], requireAll = true)
    fun handleMoreLayoutHeight(view: View, handleHeight: Int, clearHeight: Boolean) {
        if (clearHeight) {
            view.gone()
            return
        }
        view.visible()
        if (handleHeight > 0) {
            view.setHeight(handleHeight)
        }
    }
}