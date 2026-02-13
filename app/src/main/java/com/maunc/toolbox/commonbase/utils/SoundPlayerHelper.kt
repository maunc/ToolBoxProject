package com.maunc.toolbox.commonbase.utils

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.ext.audioManager

const val TURN_TABLE_ANIM_SOUND_ID = 0

@SuppressLint("ObsoleteSdkInt")
class SoundPlayerHelper {

    private val soundIdMap = HashMap<Int, Int>()

   private val soundPoolPlayer: SoundPool by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME) // 根据场景设置
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder()
                .setMaxStreams(100) // 设置最大并发播放数
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            @Suppress("DEPRECATION")
            SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        }
    }

    /**
     * 预加载音频文件
     * @param resId 音频资源ID（如R.raw.test_sound）
     * @param tag 自定义标识（用于后续播放）
     */
    fun loadSound(resId: Int, tag: Int) {
        val soundId = soundPoolPlayer.load(ToolBoxApplication.app, resId, 1)
        soundIdMap[tag] = soundId
    }

    /**
     * 播放系统内置UI音效
     * @param soundType 音效类型(AudioManager.FX_KEY_CLICK)
     */
    fun playSystemUiSound(soundType: Int) {
        val audioManager = ToolBoxApplication.app.audioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioManager?.playSoundEffect(soundType, 1.0f)
        } else {
            audioManager?.playSoundEffect(soundType)
        }
    }

    /**
     * 播放指定音频
     * @param tag 预加载时的自定义标识
     * @param loop 循环次数（0=不循环，-1=无限循环）
     * @param volume 音量（0.0~1.0）
     */
    fun playSound(tag: Int, loop: Int = 0, volume: Float = 1.0f) {
        val soundId = soundIdMap[tag] ?: return
        // 音频ID、左音量、右音量、优先级、循环次数、播放速率（1.0=正常）
        soundPoolPlayer.play(soundId, volume, volume, 1, loop, 1.0f)
    }

    fun stopSound(tag: Int) {
        val soundId = soundIdMap[tag] ?: return
        soundPoolPlayer.stop(soundId)
    }

    fun release() {
        soundPoolPlayer.release()
        soundIdMap.clear()
    }
}