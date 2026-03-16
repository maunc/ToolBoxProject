package com.maunc.toolbox.ftp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.maunc.ftp.ftpServerManager
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_NOTICE_DIALOG
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ui.dialog.CommonNoticeDialog
import com.maunc.toolbox.databinding.ActivityFtpMainBinding
import com.maunc.toolbox.ftp.adapter.FtpConfigAdapter
import com.maunc.toolbox.ftp.viewmodel.FtpMainViewModel

class FtpMainActivity : BaseActivity<FtpMainViewModel, ActivityFtpMainBinding>() {

    private val ftpConfigAdapter by lazy {
        FtpConfigAdapter()
    }

    private val tipsDialog by lazy {
        CommonNoticeDialog().setTitle(obtainString(R.string.common_tips_title_tv))
            .setContentText(obtainString(R.string.ftp_main_tips_content_tv))
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.tool_box_item_ftp_text)
        mDatabind.commonToolBar.commonToolBarCompatButton.setImageResource(R.drawable.icon_explanation)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.clickScale {
            tipsDialog.show(supportFragmentManager, COMMON_NOTICE_DIALOG)
        }
        mDatabind.ftpMainControllerConnect.clickScale {
            val portNumber = ftpConfigAdapter.data[0].content.toInt()
            val useNumber = ftpConfigAdapter.data[1].content
            val passWord = ftpConfigAdapter.data[2].content
            val shareDir = ftpConfigAdapter.data[3].content
            val (connectResult, message) = ftpServerManager.startFtpServer(
                port = portNumber,
                username = useNumber,
                password = passWord,
                shareDir = shareDir
            )
            mDatabind.ftpMainControllerStatusText.text =
                if (connectResult) "创建连接成功!\n地址:${message}" else "创建连接失败:${message}"
        }
        mDatabind.ftpMainControllerDisconnect.clickScale {
            ftpServerManager.stopFtpServer()
            mDatabind.ftpMainControllerStatusText.text = "断开连接"

        }
        mDatabind.ftpMainControllerNone.clickScale {
            mDatabind.ftpMainControllerStatusText.text =
                "状态:${ftpServerManager.isServerRunning()},${ftpServerManager.obtainServerStatusDesc()}"
        }
        mDatabind.ftpMainConfigRecycler.adapter = ftpConfigAdapter
        mDatabind.ftpMainConfigRecycler.layoutManager = linearLayoutManager()
        ftpConfigAdapter.setList(mViewModel.ftpConfigList)
    }

    override fun createObserver() {

    }

    override fun onDestroy() {
        super.onDestroy()
        ftpServerManager.stopFtpServer()
    }
}