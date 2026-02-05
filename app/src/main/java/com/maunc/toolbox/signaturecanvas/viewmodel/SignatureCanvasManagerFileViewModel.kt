package com.maunc.toolbox.signaturecanvas.viewmodel

import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.signaturecanvas.constant.saveFileNamePrefix
import com.maunc.toolbox.signaturecanvas.data.CanvasSaveFileData
import kotlinx.coroutines.launch

class SignatureCanvasManagerFileViewModel : BaseViewModel<BaseModel>() {

    private val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    private val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"

    private val selectionArgs = arrayOf("%${saveFileNamePrefix}%")

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.DATE_ADDED
    )

    private val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    // 需要收集的列表
    var saveFileList = MutableLiveData<List<CanvasSaveFileData>>()

    // 是否有数据
    var isSaveFileData = MutableLiveData(true)

    /**
     * uri 数据库
     * projection 查询内容
     * selection 过滤语句
     * selectionArgs 替换过滤语句
     * sortOrder 排序语句
     */
    fun initSaveFile() {
        viewModelScope.launch {
            val cursor = ToolBoxApplication.app.contentResolver.query(
                uri, projection, selection, selectionArgs, sortOrder
            )
            // use内部实现了close
            saveFileList.value = cursor?.use {
                val transferList = mutableListOf<CanvasSaveFileData>()
                while (it.moveToNext()) {
                    // 获取列索引
                    val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val widthIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                    val heightIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                    val dateIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                    val id = it.getLong(idIndex)
                    // 构建图片URI（标准写法，兼容分区存储）
                    val imageUri = Uri.withAppendedPath(uri, id.toString()).toString()
                    val displayName = it.getString(nameIndex)
                    val size = it.getLong(sizeIndex)
                    val width = it.getInt(widthIndex)
                    val height = it.getInt(heightIndex)
                    val dateAdded = it.getLong(dateIndex)
                    transferList.add(
                        CanvasSaveFileData(
                            id = id,
                            uri = imageUri,
                            width = width,
                            height = height,
                            fileName = displayName,
                            fileSize = size,
                            addDateTime = dateAdded
                        )
                    )
                }
                return@use transferList
            }
        }
    }
}