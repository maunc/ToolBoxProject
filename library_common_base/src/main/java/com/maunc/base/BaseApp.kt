package com.maunc.base

import android.app.Application
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.cancellation.CancellationException

open class BaseApp : Application() {

    companion object {
        lateinit var app: Application
        private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            if (throwable is CancellationException) {
                // 协程取消异常，无需处理（正常流程）
                return@CoroutineExceptionHandler
            }
        }
        val mGlobalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}