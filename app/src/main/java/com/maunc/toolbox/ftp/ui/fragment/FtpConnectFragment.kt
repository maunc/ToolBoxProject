package com.maunc.toolbox.ftp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.maunc.ftp.ConnectConfig
import com.maunc.ftp.ftpConnectManager
import com.maunc.toolbox.commonbase.base.BaseFragment
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.databinding.FragmentFtpConnectBinding
import com.maunc.toolbox.ftp.adapter.FtpConnectConfigAdapter
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

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
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
                    mDatabind.ftpConnectControllerStatusText.text = "$result"
                }
            }
        }
        mDatabind.ftpConnectControllerDisconnect.clickScale {
            ftpConnectManager.disconnect()
            mDatabind.ftpConnectControllerStatusText.text = "断开连接"
        }
        mDatabind.ftpConnectControllerStatusText.clickScale {
            lifecycleScope.launch(Dispatchers.IO) {
                val list = ftpConnectManager.list()
                Log.e("ww", "list:${list}")
            }
        }
        mDatabind.ftpConnectConfigRecycler.adapter = ftpConnectConfigAdapter
        mDatabind.ftpConnectConfigRecycler.layoutManager = activity?.linearLayoutManager()
        ftpConnectConfigAdapter.setList(mViewModel.ftpConnectConfigList)
    }

    override fun lazyLoadData() {

    }

    override fun onRestartFragment() {

    }

    override fun createObserver() {

    }
}