package com.maunc.toolbox.ftp.ui.activity

import android.os.Bundle
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.enterActivityAnim
import com.maunc.base.ext.finishCurrentResultToActivity
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.obtainIntentPutData
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.base.utils.KeyBroadUtils
import com.maunc.toolbox.databinding.ActivityFtpServerConfigBinding
import com.maunc.toolbox.ftp.adapter.FtpServerConfigAdapter
import com.maunc.toolbox.ftp.constant.RESULT_SOURCE_FROM_FTP_SERVER_CONFIG_PAGE
import com.maunc.toolbox.ftp.ui.fragment.FtpServerFragment
import com.maunc.toolbox.ftp.viewmodel.FtpServerConfigViewModel

class FtpServerConfigActivity :
    BaseActivity<FtpServerConfigViewModel, ActivityFtpServerConfigBinding>() {
    private val ftpServerConfigAdapter by lazy {
        FtpServerConfigAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        enterActivityAnim(R.anim.enter_new_data_page_anim)
        KeyBroadUtils.registerKeyBoardHeightListener(this) { keyBoardHeight ->
            mViewModel.updateNewGroupLayout(keyBoardHeight, mDatabind.ftpServerConfigMain)
        }
        mDatabind.ftpServerConfigCancelButton.clickScale {
            baseFinishCurrentActivity()
        }
        mDatabind.ftpServerConfigSureButton.clickScale {
            baseFinishCurrentActivity()
        }
        mDatabind.ftpServerConfigRecycler.adapter = ftpServerConfigAdapter
        mDatabind.ftpServerConfigRecycler.layoutManager = linearLayoutManager()
        ftpServerConfigAdapter.setList(
            mViewModel.obtainFtpServerConfigList(
                port = intent?.getIntExtra(FtpServerFragment.FTP_PORT_KEY, 0) ?: 0,
                user = intent?.getStringExtra(FtpServerFragment.FTP_USER_KEY) ?: "",
                passWord = intent?.getStringExtra(FtpServerFragment.FTP_PASS_WORD_KEY) ?: "",
                shareDir = intent?.getStringExtra(FtpServerFragment.FTP_SHARE_DIR_KEY) ?: ""
            )
        )
    }

    override fun createObserver() {}

    override fun onBackPressCallBack() {
        baseFinishCurrentActivity()
    }

    private fun baseFinishCurrentActivity(action: () -> Unit = {}) {
        action()
        finishCurrentResultToActivity(
            resultCode = RESULT_SOURCE_FROM_FTP_SERVER_CONFIG_PAGE,
            exitAnim = R.anim.exit_new_data_page_anim,
            intent = obtainIntentPutData(mutableMapOf<String, Any>().apply {
                put(FtpServerFragment.FTP_PORT_KEY, ftpServerConfigAdapter.data[0].content.toInt())
                put(FtpServerFragment.FTP_USER_KEY, ftpServerConfigAdapter.data[1].content)
                put(FtpServerFragment.FTP_PASS_WORD_KEY, ftpServerConfigAdapter.data[2].content)
                put(FtpServerFragment.FTP_SHARE_DIR_KEY, ftpServerConfigAdapter.data[3].content)
            })
        )
    }

    override fun onDestroy() {
        KeyBroadUtils.unRegisterKeyBoardHeightListener(this)
        super.onDestroy()
    }
}