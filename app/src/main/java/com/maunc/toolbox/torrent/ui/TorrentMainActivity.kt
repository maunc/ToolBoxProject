package com.maunc.toolbox.torrent.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.maunc.base.ext.clickScale
import com.maunc.base.ext.finishCurrentActivity
import com.maunc.base.ext.linearLayoutManager
import com.maunc.base.ext.toastShort
import com.maunc.base.ui.BaseActivity
import com.maunc.toolbox.R
import com.maunc.base.utils.formatFileSize
import com.maunc.toolbox.databinding.ActivityTorrentMainBinding
import com.maunc.toolbox.torrent.adapter.TorrentFileAdapter
import com.maunc.toolbox.torrent.viewmodel.TorrentMainViewModel
import kotlin.math.roundToInt

class TorrentMainActivity : BaseActivity<TorrentMainViewModel, ActivityTorrentMainBinding>() {

    private val torrentAdapter by lazy {
        TorrentFileAdapter().apply {
            setOnItemClickListener { _, _, position ->
                getItem(position).let { item ->
                    mViewModel.downloadTorrentFile(item.filePath)
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.torrentMainViewModel = mViewModel
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            getString(R.string.tool_box_item_torrent_parse_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            finishCurrentActivity()
        }
        mDatabind.commonToolBar.commonToolBarCompatButton.apply {
            visibility = View.VISIBLE
            setImageResource(android.R.drawable.ic_menu_add)
            clickScale { showMagnetInputDialog() }
        }
        mDatabind.torrentMainRecycler.layoutManager = linearLayoutManager()
        mDatabind.torrentMainRecycler.adapter = torrentAdapter
        mDatabind.torrentMainRefreshLayout.setOnRefreshListener {
            mViewModel.initTorrentFileList()
        }
        mViewModel.initTorrentFileList()
        handleLaunchMagnet(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLaunchMagnet(intent)
    }

    override fun createObserver() {
        mViewModel.torrentFileList.observe(this) { list ->
            mDatabind.torrentMainRefreshLayout.finishRefresh()
            torrentAdapter.setList(list ?: mutableListOf())
        }
        mViewModel.uiMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                toastShort(msg)
            }
        }
        mViewModel.magnetProgressState.observe(this) { p ->
            if (p == null) {
                mDatabind.torrentMagnetProgressPanel.visibility = View.GONE
                return@observe
            }
            mDatabind.torrentMagnetProgressPanel.visibility = View.VISIBLE
            val title = p.torrentName?.takeIf { it.isNotBlank() }
                ?: getString(R.string.torrent_magnet_progress_title)
            mDatabind.torrentMagnetProgressTitleTv.text = title
            if (p.phase == 0) {
                mDatabind.torrentMagnetProgressBar.isIndeterminate = true
            } else {
                mDatabind.torrentMagnetProgressBar.isIndeterminate = false
                val prog = (p.progress01 * 10000f).roundToInt().coerceIn(0, 10_000)
                mDatabind.torrentMagnetProgressBar.progress = prog
            }
            val rate = formatFileSize(p.downloadRateBytesPerSec)
            val done = formatFileSize(p.totalDoneBytes)
            val total = if (p.totalBytes > 0L) formatFileSize(p.totalBytes) else "—"
            mDatabind.torrentMagnetProgressDetailTv.text = buildString {
                append(p.stateDescription)
                append('\n')
                append(getString(R.string.torrent_magnet_progress_speed_sz, "$rate/s"))
                append("  ·  ")
                append(getString(R.string.torrent_magnet_progress_done_sz, done, total))
            }
        }
    }

    private fun showMagnetInputDialog() {
        val padding = (24 * resources.displayMetrics.density).toInt()
        val edit = EditText(this).apply {
            hint = getString(R.string.torrent_magnet_dialog_hint)
            setPadding(padding, padding, padding, padding)
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.torrent_magnet_dialog_title)
            .setView(edit)
            .setPositiveButton(R.string.torrent_magnet_dialog_confirm) { d, _ ->
                mViewModel.downloadMagnet(edit.text?.toString().orEmpty())
                d.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun handleLaunchMagnet(intent: Intent?) {
        val uri = intent?.dataString ?: return
        if (uri.startsWith("magnet:", ignoreCase = true)) {
            mViewModel.downloadMagnet(uri)
        }
    }
}
