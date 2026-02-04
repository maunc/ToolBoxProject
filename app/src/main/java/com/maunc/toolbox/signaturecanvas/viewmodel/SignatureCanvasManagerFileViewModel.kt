package com.maunc.toolbox.signaturecanvas.viewmodel

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.signaturecanvas.constant.saveFileNamePrefix
import kotlinx.coroutines.launch

class SignatureCanvasManagerFileViewModel : BaseViewModel<BaseModel>() {

    fun initSaveFile() {
        Log.e("ww", "initSaveFile")
        viewModelScope.launch {
            val cursor = ToolBoxApplication.app.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT,
                    MediaStore.Images.Media.DATE_ADDED
                ),
                "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?",
                arrayOf("%${saveFileNamePrefix}%"),
                null
            )
            cursor?.use {
                while (it.moveToNext()) {
                    // 获取列索引
                    val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val widthIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                    val heightIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                    val dateIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                    // 解析数据
                    val id = it.getLong(idIndex)
                    // 构建图片URI（标准写法，兼容分区存储）
                    val imageUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    ).toString()
                    val displayName = it.getString(nameIndex)
                    val size = it.getLong(sizeIndex)
                    val width = it.getInt(widthIndex)
                    val height = it.getInt(heightIndex)
                    val dateAdded = it.getLong(dateIndex)
                    Log.e(
                        "ww",
                        "${imageUri},${displayName},${size},${width},${height},${dateAdded}"
                    )
                }
                cursor.close()
            }
        }
    }
}