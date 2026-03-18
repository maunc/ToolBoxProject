package com.maunc.toolbox.ftp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.ftp.ftpServerManager
import com.maunc.toolbox.commonbase.base.BaseFragment
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.databinding.FragmentFtpServerBinding
import com.maunc.toolbox.ftp.adapter.FtpServerConfigAdapter
import com.maunc.toolbox.ftp.viewmodel.FtpServerViewModel

class FtpServerFragment : BaseFragment<FtpServerViewModel, FragmentFtpServerBinding>() {

    companion object {

        @JvmStatic
        fun newInstance(): FtpServerFragment {
            val args = Bundle()
            val fragment = FtpServerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val ftpServerConfigAdapter by lazy {
        FtpServerConfigAdapter()
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.ftpServerControllerConnect.clickScale {
            val portNumber = ftpServerConfigAdapter.data[0].content.toInt()
            val useNumber = ftpServerConfigAdapter.data[1].content
            val passWord = ftpServerConfigAdapter.data[2].content
            val shareDir = ftpServerConfigAdapter.data[3].content
            val (connectResult, message) = ftpServerManager.startFtpServer(
                port = portNumber,
                username = useNumber,
                password = passWord,
                shareDir = shareDir
            )
            mDatabind.ftpServerControllerStatusText.text =
                if (connectResult) "创建连接成功!\n地址:${message}" else "创建连接失败:${message}"
        }
        mDatabind.ftpServerControllerDisconnect.clickScale {
            ftpServerManager.stopFtpServer()
            mDatabind.ftpServerControllerStatusText.text = "断开连接"

        }
        mDatabind.ftpServerControllerNone.clickScale {
            mDatabind.ftpServerControllerStatusText.text =
                "状态:${ftpServerManager.isServerRunning()},${ftpServerManager.obtainServerStatusDesc()}"
        }
        mDatabind.ftpServerConfigRecycler.adapter = ftpServerConfigAdapter
        mDatabind.ftpServerConfigRecycler.layoutManager = activity?.linearLayoutManager()
        ftpServerConfigAdapter.setList(mViewModel.ftpServerConfigList)
    }

    override fun lazyLoadData() {

    }

    override fun onRestartFragment() {

    }

    override fun createObserver() {

    }
}