package com.maunc.toolbox.localfile.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.base.BaseActivity
import com.maunc.toolbox.commonbase.ext.clickScale
import com.maunc.toolbox.commonbase.ext.finishCurrentActivity
import com.maunc.toolbox.commonbase.ext.linearLayoutManager
import com.maunc.toolbox.commonbase.ext.obtainString
import com.maunc.toolbox.commonbase.ext.postSmoothScrollToPosition
import com.maunc.toolbox.commonbase.ext.toastShort
import com.maunc.toolbox.commonbase.utils.obtainSDCardRootPath
import com.maunc.toolbox.databinding.ActivityLocalFileMainBinding
import com.maunc.toolbox.localfile.adapter.LocalFileDataAdapter
import com.maunc.toolbox.localfile.adapter.LocalFilePathGuideAdapter
import com.maunc.toolbox.localfile.data.LocalFileData
import com.maunc.toolbox.localfile.data.LocalFilePathGuideData
import com.maunc.toolbox.localfile.viewmodel.LocalFileMainViewModel
import java.io.File
import java.util.ArrayDeque

class LocalFileMainActivity : BaseActivity<LocalFileMainViewModel, ActivityLocalFileMainBinding>() {

    private val fileStack = ArrayDeque<String>()

    private val localFileAdapter by lazy {
        LocalFileDataAdapter().apply {
            setOnItemClickListener { _, _, position ->
                val item = data.getOrNull(position) ?: return@setOnItemClickListener
                val currentPath = fileStack.last()
                val nextPath = File(currentPath, item.fileName).absolutePath
                if (item.fileType == LocalFileData.LOCAL_FILE_TYPE_DIR) {
                    fileStack.addLast(nextPath)
                    mViewModel.loadDir(nextPath)
                } else {
                    toastShort(item.absolutePath)
                }
            }
        }
    }

    private val pathGuideAdapter by lazy {
        LocalFilePathGuideAdapter().apply {
            setOnItemClickListener { _, _, position ->
                val item = data.getOrNull(position) ?: return@setOnItemClickListener
                val root = fileStack.firstOrNull() ?: return@setOnItemClickListener
                val crumbs = buildPathBreadcrumb(root, item.realPath)
                fileStack.clear()
                crumbs.forEach { fileStack.addLast(it.realPath) }
                mViewModel.loadDir(item.realPath)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.commonToolBar.commonToolBarTitleTv.text =
            obtainString(R.string.tool_box_item_local_file_text)
        mDatabind.commonToolBar.commonToolBarBackButton.clickScale {
            loadLastPath()
        }
        mDatabind.localFilePathGuideRecycler.layoutManager =
            linearLayoutManager(LinearLayoutManager.HORIZONTAL)
        mDatabind.localFilePathGuideRecycler.adapter = pathGuideAdapter

        mDatabind.localFileRecycler.layoutManager = linearLayoutManager()
        mDatabind.localFileRecycler.adapter = localFileAdapter
        mDatabind.localFileRefreshLayout.setOnRefreshListener {
            mViewModel.loadDir(fileStack.last())
        }
        val startPath = obtainSDCardRootPath()
        fileStack.addLast(startPath)
        mViewModel.loadDir(startPath)
    }

    override fun onBackPressCallBack() {
        loadLastPath()
    }

    override fun createObserver() {
        mViewModel.currentPathLiveData.observe(this) { path ->
            fileStack.firstOrNull()?.let { root ->
                val crumbs = buildPathBreadcrumb(root, path)
                pathGuideAdapter.setList(crumbs)
                if (crumbs.isNotEmpty()) {
                    mDatabind.localFilePathGuideRecycler.postSmoothScrollToPosition(crumbs.size - 1)
                }
            }
        }
        mViewModel.fileListLiveData.observe(this) {
            mDatabind.localFileRefreshLayout.finishRefresh()
            localFileAdapter.setList(it)
        }
        mViewModel.errorLiveData.observe(this) { msg ->
            toastShort(msg)
        }
    }

    private fun loadLastPath() {
        if (fileStack.size <= 1) {
            finishCurrentActivity()
        } else {
            fileStack.removeLast()
            val newPath = fileStack.last()
            mViewModel.loadDir(newPath)
        }
    }

    /**
     * 从根目录到当前路径生成面包屑
     */
    private fun buildPathBreadcrumb(
        rootPath: String,
        currentPath: String,
    ): MutableList<LocalFilePathGuideData> {
        val rTrimEnd = rootPath.trimEnd('/')
        val cTrimEnd = currentPath.trimEnd('/')
        val pathGuideList = mutableListOf<LocalFilePathGuideData>()
        if (!cTrimEnd.startsWith(rTrimEnd)) {
            pathGuideList.add(LocalFilePathGuideData(cTrimEnd, File(cTrimEnd).name))
            return pathGuideList
        }
        pathGuideList.add(
            LocalFilePathGuideData(
                rTrimEnd, obtainString(R.string.tool_box_item_local_file_text)
            )
        )
        val remainder = cTrimEnd.removePrefix(rTrimEnd).trimStart('/')
        if (remainder.isEmpty()) return pathGuideList
        var acc = rTrimEnd
        for (part in remainder.split('/').filter { it.isNotEmpty() }) {
            acc = if (acc.endsWith("/")) "$acc$part" else "$acc/$part"
            pathGuideList.add(LocalFilePathGuideData(acc, part))
        }
        return pathGuideList
    }
}