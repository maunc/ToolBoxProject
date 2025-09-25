package com.maunc.toolbox.commonbase.download

import com.liulishuo.filedownloader.BaseDownloadTask

interface BaseListener {

    fun outOfSpace()

    fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int)

    fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int)

    fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int)

    fun error(task: BaseDownloadTask, e: Throwable)

    fun completed(task: BaseDownloadTask)

    fun warn(task: BaseDownloadTask)

    fun retry(task: BaseDownloadTask, ex: Throwable, retryingTimes: Int, soFarBytes: Int)
}
