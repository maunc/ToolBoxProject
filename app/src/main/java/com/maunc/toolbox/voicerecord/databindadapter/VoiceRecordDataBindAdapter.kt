package com.maunc.toolbox.voicerecord.databindadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.animateToAlpha
import com.maunc.toolbox.commonbase.ext.getString
import com.maunc.toolbox.commonbase.ext.gone
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_DOWN
import com.maunc.toolbox.voicerecord.constant.RECORD_VIEW_STATUS_UP
import com.us.mauncview.VoiceWaveView

object VoiceRecordDataBindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["handleRecordButton"], requireAll = false)
    fun handleRecordButton(textView: TextView, recordStatus: Int) {
        when (recordStatus) {
            RECORD_VIEW_STATUS_DOWN -> {
                textView.text = getString(R.string.voice_record_up_tips)
                textView.setBackgroundResource(R.drawable.bg_white_50_radius_12)
            }

            RECORD_VIEW_STATUS_UP -> {
                textView.text = getString(R.string.voice_record_down_tips)
                textView.setBackgroundResource(R.drawable.bg_white_radius_12)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["handleRecordAnim"], requireAll = false)
    fun handleRecordAnim(voiceWaveView: VoiceWaveView, recordStatus: Int) {
        when (recordStatus) {
            RECORD_VIEW_STATUS_DOWN -> {
                voiceWaveView.animateToAlpha(
                    startAlpha = 0f, endAlpha = 1f, time = 200
                ) {
                    (it as VoiceWaveView).start()
                }
            }

            RECORD_VIEW_STATUS_UP -> {
                voiceWaveView.animateToAlpha(
                    startAlpha = 1f, endAlpha = 0f, time = 200
                ) {
                    (it as VoiceWaveView).stop()
                }
            }

            else -> {
                voiceWaveView.gone()
                voiceWaveView.stop()
            }
        }
    }
}