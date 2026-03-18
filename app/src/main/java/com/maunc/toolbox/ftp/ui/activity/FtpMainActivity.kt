package com.maunc.toolbox.ftp.ui.activity

import android.os.Bundle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maunc.ftp.ftpConnectManager
import com.maunc.ftp.ftpServerManager
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.constant.COMMON_NOTICE_DIALOG
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ui.dialog.CommonNoticeDialog
import com.maunc.toolbox.databinding.ActivityFtpMainBinding
import com.maunc.toolbox.ftp.ui.fragment.FtpServerFragment
import com.maunc.toolbox.ftp.ui.fragment.FtpConnectFragment
import com.maunc.toolbox.ftp.viewmodel.FtpMainViewModel

class FtpMainActivity : BaseActivity<FtpMainViewModel, ActivityFtpMainBinding>() {

    private val tipsDialog by lazy {
        CommonNoticeDialog().setTitle(obtainString(R.string.common_tips_title_tv))
            .setContentText(obtainString(R.string.ftp_main_tips_content_tv))
    }

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
        mDatabind.ftpMainViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int) = when (position) {
                0 -> FtpServerFragment.newInstance()
                else -> FtpConnectFragment.newInstance()
            }
        }
        // 禁止左右滑动切换
        mDatabind.ftpMainViewPager.isUserInputEnabled = false
        TabLayoutMediator(
            mDatabind.ftpMainTabLayout,
            mDatabind.ftpMainViewPager,
            true
        ) { tab, pos ->
            tab.text = when (pos) {
                0 -> obtainString(R.string.ftp_main_tab_server_text)
                else -> obtainString(R.string.ftp_main_tab_connect_text)
            }
        }.attach()
    }

    override fun createObserver() {
    }

    override fun onDestroy() {
        super.onDestroy()
        ftpServerManager.stopFtpServer()
        ftpConnectManager.disconnect()
    }
}