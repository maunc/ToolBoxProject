package com.maunc.toolbox.torrent.ui

import android.os.Bundle
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.databinding.ActivityTorrentMainBinding
import com.maunc.toolbox.torrent.adapter.TorrentFileAdapter
import com.maunc.toolbox.torrent.viewmodel.TorrentMainViewModel

class TorrentMainActivity : BaseActivity<TorrentMainViewModel, ActivityTorrentMainBinding>() {

    private val torrentAdapter by lazy { TorrentFileAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.torrentMainViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.tool_box_item_torrent_parse_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.torrentMainRecycler.layoutManager = linearLayoutManager()
        mDatabind.torrentMainRecycler.adapter = torrentAdapter
        mDatabind.torrentMainRefreshLayout.setOnRefreshListener {
            mViewModel.initTorrentFileList()
        }
        mViewModel.initTorrentFileList()
    }

    override fun createObserver() {
        mViewModel.torrentFileList.observe(this) { list ->
            mDatabind.torrentMainRefreshLayout.finishRefresh()
            torrentAdapter.setList(list ?: mutableListOf())
        }
    }
}
