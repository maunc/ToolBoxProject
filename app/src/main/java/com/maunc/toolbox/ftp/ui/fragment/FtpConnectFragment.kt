package com.maunc.toolbox.ftp.ui.fragment

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.ftp.ConnectConfig
import com.maunc.ftp.ftpConnectManager
import com.maunc.toolbox.commonbase.base.BaseFragment
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.toastShort
import com.maunc.toolbox.databinding.FragmentFtpConnectBinding
import com.maunc.toolbox.ftp.adapter.FtpConnectConfigAdapter
import com.maunc.toolbox.ftp.adapter.FtpRemoteFileAdapter
import com.maunc.toolbox.ftp.adapter.FtpRemoteFilePathGuideAdapter
import com.maunc.toolbox.ftp.data.FtpRemoteFileData
import com.maunc.toolbox.ftp.data.FtpRemoteFilePathGuideData
import com.maunc.toolbox.ftp.viewmodel.FtpConnectViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.net.ftp.FTPFile
import java.util.ArrayDeque

class FtpConnectFragment : BaseFragment<FtpConnectViewModel, FragmentFtpConnectBinding>() {

    companion object {

        @JvmStatic
        fun newInstance(): FtpConnectFragment {
            val args = Bundle()
            val fragment = FtpConnectFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val ftpConnectConfigAdapter by lazy {
        FtpConnectConfigAdapter()
    }

    private val remoteFileStack = ArrayDeque<String>()

    private val remoteFileAdapter by lazy {
        FtpRemoteFileAdapter().apply {
            setOnItemClickListener { _, _, position ->
                val item = data.getOrNull(position) ?: return@setOnItemClickListener
                if (item.isDir) {
                    remoteFileStack.addLast(item.remotePath)
                    loadRemoteDir(item.remotePath)
                } else {
                    toastShort(item.remotePath)
                }
            }
        }
    }

    private val pathGuideAdapter by lazy {
        FtpRemoteFilePathGuideAdapter().apply {
            setOnItemClickListener { _, _, position ->
                val item = data.getOrNull(position) ?: return@setOnItemClickListener
                rebuildStackTo(item.realPath)
                loadRemoteDir(item.realPath)
            }
        }
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (mDatabind.ftpConnectFileContainer.visibility != android.view.View.VISIBLE) {
                isEnabled = false
                try {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } finally {
                    isEnabled = true
                }
                return
            }
            // 仍有上一级目录时，弹栈返回上一层
            if (remoteFileStack.size > 1) {
                remoteFileStack.removeLast()
                val prev = remoteFileStack.last()
                loadRemoteDir(prev)
                return
            }
            isEnabled = false
            try {
                activity?.onBackPressed()
            } finally {
                isEnabled = true
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.ftpConnectViewModel = mViewModel
        mDatabind.ftpConnectControllerConnect.clickScale {
            lifecycleScope.launch(Dispatchers.IO) {
                val result = ftpConnectManager.connect(
                    ConnectConfig(
                        host = ftpConnectConfigAdapter.data[0].content,
                        port = ftpConnectConfigAdapter.data[1].content.toInt(),
                        username = ftpConnectConfigAdapter.data[2].content,
                        password = ftpConnectConfigAdapter.data[3].content,
                    )
                )
                withContext(Dispatchers.Main) {
                    mViewModel.ftpConnectMessage.value = "$result"
                    if (result is com.maunc.ftp.Result.Success) {
                        onLoginSuccess()
                    }
                }
            }
        }
        mDatabind.ftpConnectControllerDisconnect.clickScale {
            ftpConnectManager.disconnect()
            onExitLogin()
        }
        mDatabind.ftpConnectConfigRecycler.adapter = ftpConnectConfigAdapter
        mDatabind.ftpConnectConfigRecycler.layoutManager = activity?.linearLayoutManager()
        ftpConnectConfigAdapter.setList(mViewModel.ftpConnectConfigList)

        mDatabind.ftpConnectPathGuideRecycler.layoutManager =
            activity?.linearLayoutManager(LinearLayoutManager.HORIZONTAL)
        mDatabind.ftpConnectPathGuideRecycler.adapter = pathGuideAdapter

        mDatabind.ftpConnectFileRecycler.layoutManager = activity?.linearLayoutManager()
        mDatabind.ftpConnectFileRecycler.adapter = remoteFileAdapter

        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun lazyLoadData() {

    }

    override fun onRestartFragment() {

    }

    override fun createObserver() {

    }

    private fun onLoginSuccess() {
        mViewModel.currentConnect.value = true
        remoteFileStack.clear()
        remoteFileStack.addLast("/")
        loadRemoteDir("/")
    }

    private fun onExitLogin() {
        mViewModel.currentConnect.value = false
        mViewModel.ftpConnectMessage.value = "断开连接"
        remoteFileStack.clear()
        pathGuideAdapter.setList(emptyList())
        remoteFileAdapter.setList(emptyList())
    }

    private fun rebuildStackTo(path: String) {
        remoteFileStack.clear()
        val parts = path.trimEnd('/').split('/').filter { it.isNotBlank() }
        var acc = "/"
        remoteFileStack.addLast(acc)
        for (p in parts) {
            acc = if (acc.endsWith("/")) "$acc$p" else "$acc/$p"
            remoteFileStack.addLast(acc)
        }
    }

    private fun loadRemoteDir(path: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = ftpConnectManager.list(path)
            withContext(Dispatchers.Main) {
                when (result) {
                    is com.maunc.ftp.Result.Success -> {
                        val list = result.value
                        pathGuideAdapter.setList(buildPathBreadcrumb(path).toMutableList())
                        remoteFileAdapter.setList(mapToRemoteData(path, list).toMutableList())
                        if (pathGuideAdapter.data.isNotEmpty()) {
                            mDatabind.ftpConnectPathGuideRecycler.post {
                                mDatabind.ftpConnectPathGuideRecycler.smoothScrollToPosition(
                                    pathGuideAdapter.data.size - 1
                                )
                            }
                        }
                    }

                    is com.maunc.ftp.Result.Failure -> {
                        toastShort(result.reason)
                    }
                }
            }
        }
    }

    private fun buildPathBreadcrumb(current: String): List<FtpRemoteFilePathGuideData> {
        val c = current.trimEnd('/')
        val out = mutableListOf<FtpRemoteFilePathGuideData>()
        out.add(FtpRemoteFilePathGuideData("/", ftpConnectConfigAdapter.data[0].content))
        val remainder = c.removePrefix("/").trimStart('/')
        if (remainder.isEmpty()) return out
        var acc = "/"
        for (part in remainder.split('/').filter { it.isNotEmpty() }) {
            acc = if (acc.endsWith("/")) "$acc$part" else "$acc/$part"
            out.add(FtpRemoteFilePathGuideData(acc, part))
        }
        return out
    }

    private fun mapToRemoteData(parentPath: String, files: List<FTPFile>): List<FtpRemoteFileData> {
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backCallback.remove()
    }
}