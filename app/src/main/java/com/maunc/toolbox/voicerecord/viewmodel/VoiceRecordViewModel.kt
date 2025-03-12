package com.maunc.toolbox.voicerecord.viewmodel

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Process
import android.os.VibrationEffect
import androidx.lifecycle.MutableLiveData
import com.konovalov.vad.webrtc.Vad
import com.konovalov.vad.webrtc.VadWebRTC
import com.konovalov.vad.webrtc.config.FrameSize
import com.konovalov.vad.webrtc.config.Mode
import com.konovalov.vad.webrtc.config.SampleRate
import com.maunc.toolbox.R
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.getString
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.vibrator
import com.maunc.toolbox.voicerecord.constant.DEFAULT_RECORD_TOUCH_AMPLITUDE
import com.maunc.toolbox.voicerecord.constant.DEFAULT_VIBRATOR_TIME
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class VoiceRecordViewModel : BaseViewModel<BaseModel>() {
    private companion object {
        /**====== WebRTC配置 =====*/
        private val DEFAULT_SAMPLE_RATE = SampleRate.SAMPLE_RATE_48K
        private val DEFAULT_FRAME_SIZE = FrameSize.FRAME_SIZE_1440
        private val DEFAULT_MODE = Mode.VERY_AGGRESSIVE
        private const val DEFAULT_SILENCE_DURATION_MS = 300
        private const val DEFAULT_SPEECH_DURATION_MS = 50

        /**====== AudioRecord配置 =====*/
        private const val DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        private const val DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
        private const val DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    /**录音相关*/
    private var webRTC: VadWebRTC? = null
    private var audioRecord: AudioRecord? = null
    private var cacheDir: File? = null
    private var audioFilePath: String = ""
    private var voiceThread: Thread? = null
    private lateinit var audioFile: File
    var isRecording = MutableLiveData(false)
    var isVocals = MutableLiveData<Boolean>()

    /**view相关*/
    var recordButtonTips = MutableLiveData(getString(R.string.voice_record_down_tips))

    fun createVoiceRecordConfig() {
        cacheDir = ToolBoxApplication.app.cacheDir
        audioFilePath = "${cacheDir?.absolutePath}/recorded_audio.wav"
        webRTC = Vad.builder()
            .setSampleRate(DEFAULT_SAMPLE_RATE)
            .setFrameSize(DEFAULT_FRAME_SIZE)
            .setMode(DEFAULT_MODE)
            .setSilenceDurationMs(DEFAULT_SILENCE_DURATION_MS)
            .setSpeechDurationMs(DEFAULT_SPEECH_DURATION_MS)
            .build()
        audioRecord = createAudio()
    }

    @SuppressLint("MissingPermission")
    private fun createAudio(): AudioRecord? {
        try {
            val record = AudioRecord(
                DEFAULT_AUDIO_SOURCE,
                webRTC?.sampleRate!!.value,
                DEFAULT_CHANNEL_CONFIG,
                DEFAULT_AUDIO_FORMAT,
                maxOf(
                    AudioRecord.getMinBufferSize(
                        webRTC?.sampleRate!!.value,
                        DEFAULT_CHANNEL_CONFIG,
                        DEFAULT_AUDIO_FORMAT
                    ), webRTC?.frameSize!!.value * 2
                )
            )
            return if (record.state == AudioRecord.STATE_INITIALIZED) {
                record
            } else {
                null
            }
        } catch (e: Exception) {
            e.message?.loge()
        }
        return null
    }

    fun getAudioFilePath(): String = audioFilePath

    private val runRuntime = Runnable {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
        val outputStream = FileOutputStream(audioFile)
        try {
            val size = webRTC?.frameSize?.value
            while (!Thread.interrupted() && isRecording.value!!) {
                val buffer = ShortArray(size!!)
                val bytesRead = audioRecord?.read(buffer, 0, buffer.size)
                // 在后台线程中写入音频数据
                if (bytesRead != null && bytesRead != AudioRecord.ERROR_INVALID_OPERATION) {
                    outputStream.write(shortArrayToByteArray(buffer), 0, bytesRead)
                }
                //判断是否说话了
                isVocals.postValue(webRTC?.isSpeech(buffer))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream.close()
        }
    }

    fun startRecordVoice() {
        if (isRecording.value!!) {
            "start error now recording".loge()
            return
        }
        audioFile = File(audioFilePath)
        if (audioFile.exists()) {
            audioFile.delete()
        }
        audioRecord?.let {
            isRecording.value = true
            launchVibrator()
            it.startRecording()
            voiceThread = Thread(runRuntime)
            voiceThread?.start()
        } ?: let {
            "startRecordVoice Error audio is null reset create config and restart record".loge()
            createVoiceRecordConfig()
            startRecordVoice()
        }
    }

    fun stopRecordVoice() {
        if (isRecording.value!!.not()) {
            "not start record voice no need stop".loge()
            return
        }
        destroyVoiceRecordConfig()
        writeWavHeader()
        destroyWebRtc()
    }

    fun launchVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ToolBoxApplication.app.vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    DEFAULT_VIBRATOR_TIME, DEFAULT_RECORD_TOUCH_AMPLITUDE
                )
            )
        } else {
            ToolBoxApplication.app.vibrator?.vibrate(DEFAULT_VIBRATOR_TIME)
        }
    }

    private fun writeWavHeader() {
        val file = File(audioFilePath)
        val audioFileLength = file.length()
        val totalDataLength = audioFileLength + 36
        val channels = if (DEFAULT_CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        val byteRate = 16 * webRTC?.sampleRate?.value!! * channels / 8
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLength and 0xff).toByte()
        header[5] = ((totalDataLength shr 8) and 0xff).toByte()
        header[6] = ((totalDataLength shr 16) and 0xff).toByte()
        header[7] = ((totalDataLength shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (webRTC?.sampleRate?.value!! and 0xff).toByte()
        header[25] = ((webRTC?.sampleRate?.value!! shr 8) and 0xff).toByte()
        header[26] = ((webRTC?.sampleRate?.value!! shr 16) and 0xff).toByte()
        header[27] = ((webRTC?.sampleRate?.value!! shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte()
        header[33] = 0
        header[34] = 16
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (audioFileLength and 0xff).toByte()
        header[41] = ((audioFileLength shr 8) and 0xff).toByte()
        header[42] = ((audioFileLength shr 16) and 0xff).toByte()
        header[43] = ((audioFileLength shr 24) and 0xff).toByte()
        try {
            // 创建临时文件
            val tempFile = File.createTempFile("temp", null, file.parentFile)
            // 将新数据写入临时文件
            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(header)
            }
            // 将原文件内容追加到临时文件末尾
            FileOutputStream(tempFile, true).use { outputStream ->
                FileInputStream(file).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            // 用临时文件替换原文件
            if (file.delete()) {
                tempFile.renameTo(file)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun shortArrayToByteArray(shortArray: ShortArray): ByteArray {
        val byteArray = ByteArray(shortArray.size * 2)
        for (i in shortArray.indices) {
            val shortValue = shortArray[i]
            byteArray[i * 2] = (shortValue.toInt() and 0xff).toByte()
            byteArray[i * 2 + 1] = ((shortValue.toInt() shr 8) and 0xff).toByte()
        }
        return byteArray
    }

    fun destroyVoiceRecordConfig() {
        voiceThread?.interrupt()
        isRecording.value = false
        isVocals.value = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        cacheDir = null
        voiceThread = null
    }

    fun destroyWebRtc() {
        webRTC = null
    }

    override fun onCleared() {
        destroyVoiceRecordConfig()
        destroyWebRtc()
        super.onCleared()
    }
}
