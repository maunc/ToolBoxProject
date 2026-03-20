package com.maunc.toolbox.ftp.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.marquee
import com.maunc.base.ext.obtainString
import com.maunc.base.ext.postSmoothScrollToPosition
import com.maunc.base.ui.BaseFragment
import com.maunc.ftp.ConnectConfig
import com.maunc.ftp.ftpConnectManager
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.constant.GLOBAL_NONE_STRING
import com.maunc.toolbox.databinding.FragmentFtpConnectBinding
import com.maunc.toolbox.ftp.adapter.FtpConnectConfigAdapter
import com.maunc.toolbox.ftp.adapter.FtpRemoteFileAdapter
import com.maunc.toolbox.ftp.adapter.FtpRemoteFilePathGuideAdapter
import com.maunc.toolbox.ftp.data.FtpRemoteFileData
import com.maunc.toolbox.ftp.viewmodel.FtpConnectViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val remoteFileAdapter by lazy {
        FtpRemoteFileAdapter().apply {
            setOnItemClickListener { _, _, position ->
                val item = data.getOrNull(position) ?: return@setOnItemClickListener
                if (item.isDir) {
                    mViewModel.remoteFileStack.addLast(item.remotePath)
                    mViewModel.loadRemoteDir(item.remotePath)
                } else {
                    Log.e("ww", "路径:${item.remotePath}")
                }
            }

            setOnItemLongClickListener { _, _, position ->
                val item = data.getOrNull(position) ?: return@setOnItemLongClickListener true
                if (item.isDir) return@setOnItemLongClickListener true
                showCopyMoveDialog(item)
                true
            }
        }
    }

    private val pathGuideAdapter by lazy {
        FtpRemoteFilePathGuideAdapter().apply {
            setOnItemClickListener { _, _, position ->
                val item = data.getOrNull(position) ?: return@setOnItemClickListener
                mViewModel.rebuildStackTo(item.realPath)
                mViewModel.loadRemoteDir(item.realPath)
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
            if (mViewModel.remoteFileStack.size > 1) {
                mViewModel.remoteFileStack.removeLast()
                mViewModel.loadRemoteDir(mViewModel.remoteFileStack.last())
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

    private fun showCopyMoveDialog(item: FtpRemoteFileData) {
        AlertDialog.Builder(requireContext())
            .setTitle(item.name)
            .setItems(arrayOf("复制到本地", "移动到本地", "删除文件")) { _, which ->
                when (which) {
                    0 -> mViewModel.copyRemoteFileToLocal(item, deleteRemoteAfter = false)
                    1 -> mViewModel.copyRemoteFileToLocal(item, deleteRemoteAfter = true)
                    2 -> mViewModel.deleteRemoteFile(item)
                }
            }.setNegativeButton(obtainString(R.string.common_dialog_cancel_text), null).show()
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
                        controlEncoding = "UTF-8",
                        tryEnableUtf8 = true,
                    )
                )
                withContext(Dispatchers.Main) {
                    mViewModel.ftpConnectMessage.value = "$result"
                    if (result is com.maunc.ftp.Result.Success) {
                        mViewModel.onLoginSuccess()
                    }
                }
            }
        }
        mDatabind.ftpConnectControllerDisconnect.clickScale {
            ftpConnectManager.disconnect()
            mViewModel.onExitLogin()
        }
        mDatabind.ftpConnectConfigRecycler.adapter = ftpConnectConfigAdapter
        mDatabind.ftpConnectConfigRecycler.layoutManager = activity?.linearLayoutManager()
        ftpConnectConfigAdapter.setList(mViewModel.ftpConnectConfigList)

        mDatabind.ftpConnectPathGuideRecycler.layoutManager =
            activity?.linearLayoutManager(LinearLayoutManager.HORIZONTAL)
        mDatabind.ftpConnectPathGuideRecycler.adapter = pathGuideAdapter

        mDatabind.ftpConnectFileRecycler.layoutManager = activity?.linearLayoutManager()
        mDatabind.ftpConnectFileRecycler.adapter = remoteFileAdapter
        mDatabind.ftpConnectCopyMessageTv.marquee()
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun lazyLoadData() {

    }

    override fun onRestartFragment() {

    }

    override fun createObserver() {
        mViewModel.currentConnect.observe(this) {
            if (it) {
                mViewModel.rootPathName.value = ftpConnectConfigAdapter.data[0].content
            } else {
                pathGuideAdapter.setList(emptyList())
                remoteFileAdapter.setList(emptyList())
                mViewModel.ftpCopyMessage.value = GLOBAL_NONE_STRING
            }
        }
        mViewModel.pathGuideList.observe(this) {
            pathGuideAdapter.setList(it)
            if (pathGuideAdapter.data.isNotEmpty()) {
                mDatabind.ftpConnectPathGuideRecycler.postSmoothScrollToPosition(pathGuideAdapter.data.size - 1)
            }
        }
        mViewModel.remoteFileList.observe(this) {
            remoteFileAdapter.setList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backCallback.remove()
    }
}