package com.maunc.toolbox.torrent.viewmodel

import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maunc.base.BaseApp
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.toolbox.R
import com.maunc.toolbox.torrent.data.TorrentFileData
import com.maunc.torrent.TorrentDownloadProgress
import com.maunc.torrent.TorrentManager
import com.maunc.torrent.defaultTorrentSaveDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class TorrentMainViewModel : BaseViewModel<BaseModel>() {

    val torrentFileList =
        MutableLiveData<MutableList<TorrentFileData>>(mutableListOf())

    /** Toast / Snackbar 提示（下载结果） */
    val uiMessage = MutableLiveData<String>()

    /** 磁链下载实时进度；null 表示无进行中任务 */
    val magnetProgressState = MutableLiveData<TorrentDownloadProgress?>()

    private var magnetDownloadJob: Job? = null

    private val mediaUri: Uri = MediaStore.Files.getContentUri("external")
    private val mediaProjection = arrayOf(MediaStore.Files.FileColumns.DATA)
    private val mediaSelection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?"
    private val mediaArgs = arrayOf("%.torrent")

    /**
     * 扫描媒体库中所有 .torrent
     */
    fun initTorrentFileList() {
        viewModelScope.launch(Dispatchers.IO) {
            val pathSet = LinkedHashSet<String>()
            BaseApp.app.contentResolver.query(
                mediaUri, mediaProjection, mediaSelection, mediaArgs, null,
            )?.use { cursor ->
                val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                while (cursor.moveToNext()) {
                    val path = cursor.getString(dataIndex) ?: continue
                    if (path.lowercase().endsWith(".torrent")) {
                        pathSet.add(path)
                    }
                }
            }
            val list = pathSet.map { p ->
                val f = File(p)
                TorrentFileData(
                    filePath = p,
                    fileName = f.name,
                    fileSizeBytes = if (f.exists() && f.isFile) f.length() else 0L,
                )
            }.sortedBy { it.fileName.lowercase() }.toMutableList()
            torrentFileList.postValue(list)
        }
    }

    override fun onCleared() {
        magnetDownloadJob?.cancel()
        super.onCleared()
    }

    fun downloadTorrentFile(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                TorrentManager.downloadTorrentFile(
                    path, defaultTorrentSaveDir(BaseApp.app)
                )
            } catch (t: Throwable) {
                uiMessage.postValue(
                    t.message ?: obtainString(R.string.torrent_download_failed),
                )
            }
        }
    }

    /**
     * 磁链下载：带 [magnetProgressState] 实时进度，在 IO 线程轮询 libtorrent。
     *
     * @param onMagnetProgress 可选额外回调（与 UI 同步触发；在后台线程调用，若更新 UI 请切主线程）。
     */
    fun downloadMagnet(
        magnetUri: String,
        onMagnetProgress: ((TorrentDownloadProgress) -> Unit)? = null,
    ) {
        val uri = magnetUri.trim()
        if (uri.isEmpty()) {
            return
        }
        magnetDownloadJob?.cancel()
        magnetDownloadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                TorrentManager.downloadMagnetWithProgress(
                    magnetUri = uri,
                    saveDir = defaultTorrentSaveDir(BaseApp.app),
                    cancel = { !isActive },
                ) { p ->
                    magnetProgressState.postValue(p)
                    onMagnetProgress?.invoke(p)
                }
                magnetProgressState.postValue(null)
            } catch (e: CancellationException) {
                magnetProgressState.postValue(null)
                throw e
            } catch (t: Throwable) {
                magnetProgressState.postValue(null)
                uiMessage.postValue(
                    t.message ?: obtainString(R.string.torrent_download_failed),
                )
            }
        }
    }
}
