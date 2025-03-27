package com.maunc.toolbox.chatroom.viewmodel

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.VibrationEffect
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_LAYOUT_UPDATE_TIME
import com.maunc.toolbox.chatroom.constant.CHAT_ROOM_TEXT_TYPE
import com.maunc.toolbox.chatroom.constant.DEFAULT_RECORD_TOUCH_AMPLITUDE
import com.maunc.toolbox.chatroom.constant.DEFAULT_VIBRATOR_TIME
import com.maunc.toolbox.chatroom.constant.RECORD_VIEW_STATUS_UP
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.inputMethodManager
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.vibrator
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ChatRoomViewModel : BaseViewModel<BaseModel>() {
    private companion object {
        /**====== AudioRecord配置 =====*/
        private const val DEFAULT_SAMPLE_RATE = 44100
        private const val DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        private const val DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val DEFAULT_BUFFER_SIZE = AudioRecord.getMinBufferSize(
            DEFAULT_SAMPLE_RATE,
            DEFAULT_CHANNEL_CONFIG,
            DEFAULT_AUDIO_FORMAT
        )
    }

    /**录音相关*/
    private var audioRecord: AudioRecord? = null
    private var cacheDir: File? = null
    private var audioFilePath: String = ""
    private var voiceThread: Thread? = null
    private lateinit var audioFile: File
    private var isRecording = MutableLiveData(false)
    var isWriteWavHeader = MutableLiveData(false)

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    /**view相关*/
    var chatRoomType = MutableLiveData(CHAT_ROOM_TEXT_TYPE)
    var recordViewStatus = MutableLiveData(RECORD_VIEW_STATUS_UP) // 录音状态
    var editLength = MutableLiveData(0)
    var editMaxInputNum = MutableLiveData(25)
    val chatHandler = Handler(Looper.getMainLooper())

    fun createVoiceRecordConfig() {
        cacheDir = ToolBoxApplication.app.cacheDir
        val externalStorageState = Environment.getExternalStorageState()
        "sdCardPath:${externalStorageState}".loge()
        audioFilePath = "${cacheDir?.absolutePath}/recorded_audio.wav"
        "voiceRecordPath:${audioFilePath}".loge()
        audioRecord = createAudio()
    }

    @SuppressLint("MissingPermission")
    private fun createAudio(): AudioRecord? {
        try {
            val record = AudioRecord(
                DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLE_RATE,
                DEFAULT_CHANNEL_CONFIG,
                DEFAULT_AUDIO_FORMAT,
                DEFAULT_BUFFER_SIZE
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
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(audioFile)
            while (isRecording.value!!) {
                val bytesRead = audioRecord?.read(buffer, 0, DEFAULT_BUFFER_SIZE)
                if (bytesRead != null && bytesRead != AudioRecord.ERROR_INVALID_OPERATION) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
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
        if (isWriteWavHeader.value!!) {
            writeWavHeader()
        }
    }

    fun playerWavFilePath(file: File) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        try {
            mediaPlayer.setDataSource(
                ToolBoxApplication.app,
                Uri.fromFile(file)
            )
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun editRequestFocusable(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
    }

    fun showSoftInputKeyBoard(editText: EditText) {
        chatHandler.postDelayed({
            val inputManger = ToolBoxApplication.app.inputMethodManager
            inputManger?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            editRequestFocusable(editText)
        }, CHAT_ROOM_LAYOUT_UPDATE_TIME)
    }

    fun hideSoftInputKeyBoard(editText: EditText) {
        chatHandler.postDelayed({
            val inputManger = ToolBoxApplication.app.inputMethodManager
            inputManger?.hideSoftInputFromWindow(editText.windowToken, 0)
        }, CHAT_ROOM_LAYOUT_UPDATE_TIME)
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
        val byteRate = 16 * DEFAULT_SAMPLE_RATE * channels / 8
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
        header[24] = (DEFAULT_SAMPLE_RATE and 0xff).toByte()
        header[25] = ((DEFAULT_SAMPLE_RATE shr 8) and 0xff).toByte()
        header[26] = ((DEFAULT_SAMPLE_RATE shr 16) and 0xff).toByte()
        header[27] = ((DEFAULT_SAMPLE_RATE shr 24) and 0xff).toByte()
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

    fun ShortArray.shortArrayToByteArray(): ByteArray {
        val byteArray = ByteArray(this.size * 2)
        for (i in this.indices) {
            val shortValue = this[i]
            byteArray[i * 2] = (shortValue.toInt() and 0xff).toByte()
            byteArray[i * 2 + 1] = ((shortValue.toInt() shr 8) and 0xff).toByte()
        }
        return byteArray
    }

    private fun destroyVoiceRecordConfig() {
        voiceThread?.interrupt()
        isRecording.value = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        cacheDir = null
        voiceThread = null
    }

    override fun onCleared() {
        destroyVoiceRecordConfig()
        chatHandler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}
