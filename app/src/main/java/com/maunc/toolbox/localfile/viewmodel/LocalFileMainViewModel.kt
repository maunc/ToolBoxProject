package com.maunc.toolbox.localfile.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.localfile.data.LocalFileData
import java.io.File

class LocalFileMainViewModel : BaseViewModel<BaseModel>() {

    val currentPathLiveData = MutableLiveData<String>()
    val fileListLiveData = MutableLiveData<MutableList<LocalFileData>>()
    val errorLiveData = MutableLiveData<String>()

    /**
     * 列出指定路径下的文件/文件夹（目录优先）
     */
    fun loadDir(path: String) {
        launch(
            block = {
                val dir = File(path)
                val list = dir.listFiles()?.toMutableList().orEmpty()

                val sorted = list.sortedWith(
                    compareBy<File> { !it.isDirectory }.thenBy { it.name.lowercase() }
                )
                sorted.mapNotNull { file->
                    val name = file.name ?: return@mapNotNull null
                    if (name.isBlank()) return@mapNotNull null
                    val fileExtension =  file.extension.takeIf { it.isNotBlank() }
                    val fileType = if (file.isDirectory) {
                        LocalFileData.LOCAL_FILE_TYPE_DIR
                    } else {
                        LocalFileData.getFileTypeFromExtension(fileExtension)
                    }
                    LocalFileData(
                        fileType = fileType,
                        fileName = name,
                        absolutePath = file.absolutePath,
                        sizeBytes = if (file.isDirectory) 0L else file.length(),
                        lastModifiedMillis = file.lastModified(),
                        extension = fileExtension,
                        isHidden = file.isHidden,
                        canRead = file.canRead(),
                        canWrite = file.canWrite(),
                        canExecute = file.canExecute(),
                        parentPath = file.parentFile?.absolutePath,
                        exists = file.exists(),
                    )
                }.toMutableList()
            },
            success = { data ->
                currentPathLiveData.postValue(path)
                fileListLiveData.postValue(data)
            },
            error = { t ->
                errorLiveData.postValue(t.message ?: "列目录失败")
            }
        )
    }
}