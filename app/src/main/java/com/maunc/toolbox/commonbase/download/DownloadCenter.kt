package com.maunc.toolbox.commonbase.download

import android.app.Application
import android.os.Environment
import android.os.StatFs
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadLog
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.maunc.toolbox.commonbase.ext.loge

/**
 * 主题包下载器
 */
object DownloadCenter {

    const val MEMORY_SIZE = 512

    @JvmStatic
    fun getInternalMemorySize(): Long {
        val dataPath = Environment.getDataDirectory()
        val dataFs = StatFs(dataPath.path)
        val sizes = dataFs.freeBlocksLong * dataFs.blockSizeLong
        return sizes / (1024 * 1024)
    }

    fun init(application: Application, frequency: Int = 100, isOpen: Boolean = false) {
        FileDownloader.setGlobalPost2UIInterval(frequency)
        FileDownloadLog.NEED_LOG = isOpen
        FileDownloader.setupOnApplicationOnCreate(application)
        //提前启动下载服务
        FileDownloader.getImpl().bindService {
            "JDDownloader connectService success".loge()
        }
    }

    fun start(
        downloadItem: DownloadItem,
        downloadListener: DownloadListener,
        retryCount: Int = 1,
    ) {
        if (getInternalMemorySize() < MEMORY_SIZE + (downloadItem.size / 1024)) {
            downloadListener.outOfSpace()
            return
        }
        //        pending -> started -> connected -> (progress <->progress) -> blockComplete -> completed
        //        可能会遇到以下回调而直接终止整个下载过程:
        //        paused / completed / error / warn
        "start download task 【 url = " + downloadItem.url + " | path = " +
                downloadItem.getAbsoluteDownloadPath() + " 】".loge()
        //需要保留taskId与
        FileDownloader
            .getImpl()
            .create(downloadItem.url)
            .setPath(downloadItem.getAbsoluteDownloadPath())
            .setAutoRetryTimes(retryCount)
            .setListener(object : FileDownloadListener() {
                override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    "pending 【 task id = " + task.id + "】".loge()
                    downloadListener.pending(task, soFarBytes, totalBytes)
                }

                override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    "progress【 task id = " + task.id + "| soFarBytes = " +
                            soFarBytes + "|  totalBytes = " + totalBytes + " 】".loge()
                    downloadListener.progress(task, soFarBytes, totalBytes)
                }

                override fun completed(task: BaseDownloadTask) {
                    "completed 【 task id = " + task.id + " 】".loge()
                    downloadListener.completed(task)
                }

                override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    "paused 【 task id = " + task.id + "| soFarBytes = " +
                            soFarBytes + "|  totalBytes = " + totalBytes + " 】".loge()
                    downloadListener.paused(task, soFarBytes, totalBytes)
                }

                override fun error(task: BaseDownloadTask, e: Throwable) {
                    "error 【 task id = " + task.id + "| error = " +
                            e.message + " 】".loge()
                    downloadListener.error(task, e)
                }

                override fun warn(task: BaseDownloadTask) {
                    "warn 【 task id = " + task.id + " 】".loge()
                    downloadListener.warn(task)
                }

                override fun retry(
                    task: BaseDownloadTask?,
                    ex: Throwable?,
                    retryingTimes: Int,
                    soFarBytes: Int,
                ) {
                    "warn 【 task id = " + task!!.id + " | error = " +
                            ex!!.message + "  | retryingTimes = " + retryingTimes +
                            " |  soFarBytes = " + soFarBytes + "】".loge()
                    super.retry(task, ex, retryingTimes, soFarBytes)
                    downloadListener.retry(task, ex, retryingTimes, soFarBytes)
                }
            }).start()
    }

    class BaseDownLoadListener() : FileDownloadListener() {
        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            "pending 【 task id = " + task.id + "】".loge()
//            downloadListener.pending(task, soFarBytes, totalBytes)
        }

        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            "progress【 task id = " + task.id + "| soFarBytes = " +
                    soFarBytes + "|  totalBytes = " + totalBytes + " 】".loge()
//            downloadListener.progress(task, soFarBytes, totalBytes)
        }

        override fun completed(task: BaseDownloadTask) {
            "completed 【 task id = " + task.id + " 】".loge()
//            downloadListener.completed(task)
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            "paused 【 task id = " + task.id + "| soFarBytes = " +
                    soFarBytes + "|  totalBytes = " + totalBytes + " 】".loge()
//            downloadListener.paused(task, soFarBytes, totalBytes)
        }

        override fun error(task: BaseDownloadTask, e: Throwable) {
            "error 【 task id = " + task.id + "| error = " +
                    e.message + " 】".loge()
//            downloadListener.error(task, e)
        }

        override fun warn(task: BaseDownloadTask) {
            "warn 【 task id = " + task.id + " 】".loge()
//            downloadListener.warn(task)
        }

        override fun retry(
            task: BaseDownloadTask?,
            ex: Throwable?,
            retryingTimes: Int,
            soFarBytes: Int,
        ) {
            "warn 【 task id = " + task!!.id + " | error = " +
                    ex!!.message + "  | retryingTimes = " + retryingTimes +
                    " |  soFarBytes = " + soFarBytes + "】".loge()
            super.retry(task, ex, retryingTimes, soFarBytes)
//            downloadListener.retry(task, ex, retryingTimes, soFarBytes)
        }
    }

    /**
     * 默认断点，resume只需要重新start
     */
    fun pause(id: Int) {
        FileDownloader.getImpl().pause(id)
    }

    fun pause(item: DownloadItem) {
        pause(
            FileDownloadUtils.generateId(
                item.url,
                item.getAbsoluteDownloadPath()
            )
        )
    }
}