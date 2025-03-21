package com.maunc.toolbox.voicerecord.databindadapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.animateToAlpha
import com.maunc.toolbox.commonbase.ext.getColor
import com.maunc.toolbox.commonbase.ext.getString
import com.maunc.toolbox.commonbase.ext.gone
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_DOWN
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_MOVE_CANCEL
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_MOVE_CANCEL_DONE
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_UP
import com.us.mauncview.VoiceWaveView

object VoiceRecordDataBindAdapter {

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

    @JvmStatic
    @BindingAdapter(value = ["handlerVoiceWaveView"], requireAll = false)
    fun handlerVoiceWaveView(voiceWaveView: VoiceWaveView, recordStatus: Int) {
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

    @JvmStatic
    @BindingAdapter(value = ["handleRecordAnim"], requireAll = false)
    fun handleRecordAnim(view: View, recordStatus: Int) {
        when (recordStatus) {
            RECORD_VIEW_STATUS_DOWN -> {
                view.animateToAlpha(
                    startAlpha = 0f, endAlpha = 1f, time = 200
                ) {
                    if (view is VoiceWaveView) {
                        view.start()
                    }
                }
            }

            RECORD_VIEW_STATUS_UP -> {
                view.animateToAlpha(
                    startAlpha = 1f, endAlpha = 0f, time = 100
                ) {
                    if (view is VoiceWaveView) {
                        view.stop()
                    }
                }
            }

            RECORD_VIEW_STATUS_MOVE_CANCEL -> {

            }

            RECORD_VIEW_STATUS_MOVE_CANCEL_DONE -> {

            }

            else -> {
                view.gone()
                if (view is VoiceWaveView) {
                    view.stop()
                }
            }
        }
    }
}