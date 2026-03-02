package com.maunc.toolbox.commonbase.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.maunc.unpeeklivedata.ui.callback.UnPeekLiveData

class AppStatusManager private constructor() {

    companion object {
        const val TAG = "AppStatusManager"
        val instance: AppStatusManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppStatusManager()
        }

        @JvmStatic
        fun initAppStatusManager(context: Context) {
            registerFrontBackListener()
            registerScreenListener(context)
        }

        private fun registerFrontBackListener() {
            ProcessLifecycleOwner.get().lifecycle.addObserver(FrontAndBackReceiver)
        }

        private fun registerScreenListener(context: Context) {
            context.registerReceiver(ScreenReceiver, IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            })
        }
    }

    val mFrontAndBackState = UnPeekLiveData<Boolean>()

    val mScreenState = UnPeekLiveData<Boolean>()

    object FrontAndBackReceiver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.e(TAG, "进入了前台")
            instance.mFrontAndBackState.postValue(true)
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            Log.e(TAG, "进入了后台")
            instance.mFrontAndBackState.postValue(false)
        }
    }

    object  ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_OFF == action) {
                Log.e(TAG, "息屏")
                instance.mScreenState.postValue(false)
            } else if (Intent.ACTION_SCREEN_ON == action) {
                Log.e(TAG, "亮屏")
                instance.mScreenState.postValue(true)
            }
        }
    }
}