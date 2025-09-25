package com.maunc.toolbox.commonbase.download

import com.liulishuo.filedownloader.BaseDownloadTask

abstract class DownloadListener : BaseListener {

    override fun outOfSpace() {}

    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

    override fun error(task: BaseDownloadTask, e: Throwable) {}

    override fun completed(task: BaseDownloadTask) {}

    abstract fun error(e: Throwable)

    override fun warn(task: BaseDownloadTask) {}

    override fun retry(
        task: BaseDownloadTask,
        ex: Throwable,
        retryingTimes: Int,
        soFarBytes: Int,
    ) {
    }
}
