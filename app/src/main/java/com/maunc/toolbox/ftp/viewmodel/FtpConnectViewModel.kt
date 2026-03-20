package com.maunc.toolbox.ftp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maunc.base.ext.mutableListInsert
import com.maunc.base.ext.obtainString
import com.maunc.base.ui.BaseModel
import com.maunc.base.ui.BaseViewModel
import com.maunc.ftp.ftpConnectManager
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.base.utils.obtainSDCardRootPath
import com.maunc.toolbox.ftp.data.FtpConnectConfigData
import com.maunc.toolbox.ftp.data.FtpRemoteFileData
import com.maunc.toolbox.ftp.data.FtpRemoteFilePathGuideData
import com.maunc.toolbox.ftp.data.FtpServerConfigData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.net.ftp.FTPFile
import java.io.File
import java.util.ArrayDeque

class FtpConnectViewModel : BaseViewModel<BaseModel>() {

    var currentConnect = MutableLiveData(false)//当前登录状态
    var ftpConnectMessage = MutableLiveData(GLOBAL_NONE_STRING)//提示
    var ftpCopyMessage = MutableLiveData(GLOBAL_NONE_STRING)//提示

    var remoteFileStack = ArrayDeque<String>() // 当前文件夹栈

    var rootPathName = MutableLiveData(GLOBAL_NONE_STRING) //导航的跟目录
    var pathGuideList = MutableLiveData<MutableList<FtpRemoteFilePathGuideData>>() //文件导航列表
    var remoteFileList = MutableLiveData<MutableList<FtpRemoteFileData>>()//文件实际列表

    val ftpConnectConfigList = mutableListOf<FtpConnectConfigData>().mutableListInsert(
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_host_text),
            type = FtpServerConfigData.FTP_SHARE_DIR,
            content = "10.193.202.145",
            editHint = obtainString(R.string.ftp_server_host_tips_text)
        ),
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_port_text),
            type = FtpServerConfigData.FTP_PORT_TYPE,
            content = obtainString(R.string.ftp_server_default_port_text),
            editHint = obtainString(R.string.ftp_server_port_tips_text)
        ),
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_use_text),
            type = FtpServerConfigData.FTP_USE_TYPE,
            content = obtainString(R.string.ftp_server_default_use_text),
            editHint = obtainString(R.string.ftp_server_use_tips_text)
        ),
        FtpConnectConfigData(
            title = obtainString(R.string.ftp_server_pass_word_text),
            type = FtpServerConfigData.FTP_PASS_WORD_TYPE,
            content = obtainString(R.string.ftp_server_default_pass_word_text),
            editHint = obtainString(R.string.ftp_server_pass_word_tips_text)
        ),
    )

    /**
     * 复制/移动远端文件到本地：
     * - 复制：只下载
     * - 移动：下载成功后删除远端
     */
    fun copyRemoteFileToLocal(item: FtpRemoteFileData, deleteRemoteAfter: Boolean) {
        val downloadDir = File(obtainSDCardRootPath(), "ToolBoxFtp")
        val targetFile = resolveUniqueFile(downloadDir, sanitizeFileName(item.name))
        viewModelScope.launch(Dispatchers.IO) {
            val total = item.sizeBytes.takeIf { it > 0L }
            val download = ftpConnectManager.download(
                remotePath = item.remotePath,
                localFile = targetFile,
                totalBytes = total ?: 0L,
                onProgress = { downloaded, totalBytes ->
                    val progressText = if (totalBytes != null && totalBytes > 0L) {
                        val percent = (downloaded * 100 / totalBytes).coerceIn(0, 100)
                        "下载中:${item.name} ${percent}% (${downloaded}/${totalBytes})"
                    } else {
                        "下载中:${item.name} (${downloaded} bytes)"
                    }
                    ftpCopyMessage.postValue(progressText)
                }
            )
            if (download is com.maunc.ftp.Result.Success && deleteRemoteAfter) {
                ftpConnectManager.delete(item.remotePath)
            }
            withContext(Dispatchers.Main) {
                when (download) {
                    is com.maunc.ftp.Result.Success -> {
                        if (deleteRemoteAfter) {
                            ftpCopyMessage.value = "移动成功:" + targetFile.absolutePath
                        } else {
                            ftpCopyMessage.value = "复制成功:" + targetFile.absolutePath
                        }
                        remoteFileStack.lastOrNull()?.let { loadRemoteDir(it) }
                    }

                    is com.maunc.ftp.Result.Failure -> {
                        ftpCopyMessage.value = "操作失败: ${download.reason}"
                    }
                }
            }
        }
    }

    fun deleteRemoteFile(item: FtpRemoteFileData) {
        viewModelScope.launch(Dispatchers.IO) {
            val delete = ftpConnectManager.delete(item.remotePath)
            withContext(Dispatchers.Main) {
                when (delete) {
                    is com.maunc.ftp.Result.Success -> {
                        ftpCopyMessage.value = "删除成功"
                        remoteFileStack.lastOrNull()?.let { loadRemoteDir(it) }
                    }

                    is com.maunc.ftp.Result.Failure -> {
                        ftpCopyMessage.value = delete.reason
                    }
                }
            }
        }
    }

    fun onLoginSuccess() {
        currentConnect.value = true
        remoteFileStack.clear()
        remoteFileStack.addLast("/")
        loadRemoteDir("/")
    }

    fun onExitLogin() {
        currentConnect.value = false
        ftpConnectMessage.value = "断开连接成功"
        remoteFileStack.clear()
    }

    /**
     * 更新文件栈列表
     */
    fun rebuildStackTo(path: String) {
        remoteFileStack.clear()
        val parts = path.trimEnd('/').split('/').filter { it.isNotBlank() }
        var acc = "/"
        remoteFileStack.addLast(acc)
        for (p in parts) {
            acc = if (acc.endsWith("/")) "$acc$p" else "$acc/$p"
            remoteFileStack.addLast(acc)
        }
    }

    /**
     * 加载远端文件列表
     */
    fun loadRemoteDir(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = ftpConnectManager.list(path)
            withContext(Dispatchers.Main) {
                when (result) {
                    is com.maunc.ftp.Result.Success -> {
                        val list = result.value
                        pathGuideList.value = buildPathBreadcrumb(path)
                        remoteFileList.value = buildFtpRemoteData(path, list)
                    }

                    is com.maunc.ftp.Result.Failure -> {
                        ftpCopyMessage.value = result.reason
                    }
                }
            }
        }
    }

    /**
     * 构建文件导航列表
     */
    private fun buildPathBreadcrumb(current: String): MutableList<FtpRemoteFilePathGuideData> {
        val c = current.trimEnd('/')
        val out = mutableListOf<FtpRemoteFilePathGuideData>()
        out.add(FtpRemoteFilePathGuideData("/", rootPathName.value ?: "根目录"))
        val remainder = c.removePrefix("/").trimStart('/')
        if (remainder.isEmpty()) return out
        var acc = "/"
        for (part in remainder.split('/').filter { it.isNotEmpty() }) {
            acc = if (acc.endsWith("/")) "$acc$part" else "$acc/$part"
            out.add(FtpRemoteFilePathGuideData(acc, part))
        }
        return out
    }

    /**
     * 构建远端文件列表
     */
    private fun buildFtpRemoteData(
        parentPath: String,
        files: List<FTPFile>,
    ): MutableList<FtpRemoteFileData> {
        val sorted = files.filter { it.name != "." && it.name != ".." }
            .sortedWith(compareBy<FTPFile> { !it.isDirectory }.thenBy {
                it.name?.lowercase() ?: ""
            })
        return sorted.mapNotNull { f ->
            val name = f.name ?: return@mapNotNull null
            val isDir = f.isDirectory
            val remotePath =
                if (parentPath.endsWith("/")) "$parentPath$name" else "$parentPath/$name"
            val ext = name.substringAfterLast('.', "").takeIf { it.isNotBlank() }
            val type = if (isDir) FtpRemoteFileData.LOCAL_FILE_TYPE_DIR
            else FtpRemoteFileData.getFileTypeFromExtension(ext)

            FtpRemoteFileData(
                name = name,
                remotePath = remotePath,
                isDir = isDir,
                sizeBytes = if (isDir) 0L else f.size,
                timestampMillis = runCatching { f.timestamp?.timeInMillis ?: 0L }.getOrDefault(0L),
                extension = ext,
                fileType = type,
                ftpType = f.type,
                owner = f.user,
                group = f.group,
                hardLinkCount = runCatching { f.hardLinkCount }.getOrDefault(0),
                rawListing = runCatching { f.rawListing }.getOrNull(),
            )
        }.toMutableList()
    }

    /**
     * 创建copy到本地的文件夹
     */
    private fun resolveUniqueFile(dir: File, fileName: String): File {
        dir.mkdirs()
        var candidate = File(dir, fileName)
        if (!candidate.exists()) return candidate

        val dot = fileName.lastIndexOf('.')
        val base = if (dot > 0) fileName.substring(0, dot) else fileName
        val ext = if (dot > 0) fileName.substring(dot) else ""

        var index = 1
        while (candidate.exists()) {
            candidate = File(dir, "$base($index)$ext")
            index++
        }
        return candidate
    }

    /**
     * 将远端文件名清洗为 Android 本地文件系统可用的名字
     */
    private fun sanitizeFileName(input: String): String {
        val cleaned = input
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .replace(Regex("[\\u0000-\\u001F]"), "_")
            .trim()
            .trimEnd('.')
        return cleaned.ifBlank { "file" }
    }
}