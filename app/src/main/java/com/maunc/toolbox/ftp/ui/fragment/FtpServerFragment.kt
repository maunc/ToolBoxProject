package com.maunc.toolbox.ftp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import com.maunc.ftp.ftpServerManager
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseFragment
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.obtainActivityIntentPutData
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.toast
import com.maunc.toolbox.databinding.FragmentFtpServerBinding
import com.maunc.toolbox.ftp.constant.RESULT_SOURCE_FROM_FTP_SERVER_CONFIG_PAGE
import com.maunc.toolbox.ftp.ui.activity.FtpServerConfigActivity
import com.maunc.toolbox.ftp.viewmodel.FtpServerViewModel

class FtpServerFragment : BaseFragment<FtpServerViewModel, FragmentFtpServerBinding>() {

    companion object {
        const val FTP_PORT_KEY = "port"
        const val FTP_USER_KEY = "user"
        const val FTP_PASS_WORD_KEY = "passWord"
        const val FTP_SHARE_DIR_KEY = "shareDir"

        @JvmStatic
        fun newInstance(): FtpServerFragment {
            val args = Bundle()
            val fragment = FtpServerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val ftpServerConfigActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val dataIntent = it.data!!
        if (it.resultCode == RESULT_SOURCE_FROM_FTP_SERVER_CONFIG_PAGE) {
            mViewModel.currentPort.value = dataIntent.getIntExtra(FTP_PORT_KEY, 0)
            mViewModel.currentUser.value = dataIntent.getStringExtra(FTP_USER_KEY)
            mViewModel.currentPassWord.value = dataIntent.getStringExtra(FTP_PASS_WORD_KEY)
            mViewModel.currentShareDir.value = dataIntent.getStringExtra(FTP_SHARE_DIR_KEY)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        if (ftpServerManager.isServerRunning()) {
            mDatabind.ftpServerControllerTv.text =
                obtainString(R.string.ftp_server_disconnect_text)
            setKeepScreenOn(true)
        } else {
            setKeepScreenOn(false)
        }
        mDatabind.ftpServerControllerTv.clickScale {
            if (ftpServerManager.isServerRunning()) {
                ftpServerManager.stopFtpServer()
                mDatabind.ftpServerControllerTv.text =
                    obtainString(R.string.ftp_server_connect_text)
                mDatabind.ftpServerControllerStatusText.text = "已断开连接"
                setKeepScreenOn(false)
                return@clickScale
            }
            val (connectResult, message) = ftpServerManager.startFtpServer(
                port = mViewModel.currentPort.value!!,
                username = mViewModel.currentUser.value!!,
                password = mViewModel.currentPassWord.value!!,
                shareDir = mViewModel.currentShareDir.value!!
            )
            if (!connectResult) {
                mDatabind.ftpServerControllerStatusText.text = "创建连接失败:${message}"
                setKeepScreenOn(false)
                return@clickScale
            }
            mDatabind.ftpServerControllerStatusText.text = "创建连接成功!\n地址:${message}"
            mDatabind.ftpServerControllerTv.text =
                obtainString(R.string.ftp_server_disconnect_text)
            setKeepScreenOn(true)
        }
        mDatabind.ftpServerConfigTv.clickScale {
            if (ftpServerManager.isServerRunning()) {
                toast(obtainString(R.string.ftp_server_disconnect_tips_text))
                return@clickScale
            }
            ftpServerConfigActivityResult.launch(
                activity?.obtainActivityIntentPutData(FtpServerConfigActivity::class.java,
                    mutableMapOf<String, Any>().apply {
                        put(FTP_PORT_KEY, mViewModel.currentPort.value!!)
                        put(FTP_USER_KEY, mViewModel.currentUser.value!!)
                        put(FTP_PASS_WORD_KEY, mViewModel.currentPassWord.value!!)
                        put(FTP_SHARE_DIR_KEY, mViewModel.currentShareDir.value!!)
                    }
                )
            )
        }
    }

    override fun lazyLoadData() {}

    override fun onRestartFragment() {

    }

    override fun createObserver() {}

    private fun setKeepScreenOn(enable: Boolean) {
        val window = activity?.window ?: return
        if (enable) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}