package com.maunc.toolbox.torrent.viewmodel

import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.torrent.data.TorrentFileData
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TorrentMainViewModel : BaseViewModel<BaseModel>() {

    val torrentFileList =
        MutableLiveData<MutableList<TorrentFileData>>(mutableListOf())

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
            ToolBoxApplication.app.contentResolver.query(
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
}
