package com.maunc.toolbox.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.maunc.toolbox.ext.loge
import kotlin.math.abs

object KeyBroadUtils {

    @JvmStatic
    var sDecorViewInvisibleHeightPre: Int = 0

    @JvmStatic
    var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    @JvmStatic
    var mNavHeight: Int = 0

    @JvmStatic
    var sDecorViewDelta: Int = 0

    @JvmStatic
    fun getDecorViewInvisibleHeight(activity: Activity?): Int {
        val decorView = activity?.window?.decorView
        val outRect = Rect()
        decorView?.getWindowVisibleDisplayFrame(outRect)
        val delta = abs(decorView?.bottom!! - outRect.bottom)
        if (delta <= mNavHeight) {
            sDecorViewDelta = delta
            return 0
        }
        return delta - sDecorViewDelta
    }

    @JvmStatic
    fun registerKeyBoardHeightListener(activity: Activity?, listener: KeyboardHeightListener) {
        activity?.window?.attributes?.flags?.let {
            if ((it and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
                activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
        }
        val contentView = activity?.findViewById<FrameLayout>(android.R.id.content)
        sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(activity)
        onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val height = getDecorViewInvisibleHeight(activity)
            if (sDecorViewInvisibleHeightPre != height) {
                "软键盘高度:$height".loge("KeyBroadUtils")
                listener.onKeyboardHeightChanged(height)
                sDecorViewInvisibleHeightPre = height
            }
        }
        getNavigateBarHeight(activity) { height, hasNavHeight ->
            mNavHeight = height
            contentView?.viewTreeObserver?.addOnGlobalLayoutListener(onGlobalLayoutListener)
        }
    }

    @JvmStatic
    fun unRegisterKeyBoardHeightListener(activity: Activity?) {
        onGlobalLayoutListener = null
        activity?.window?.decorView?.findViewById<FrameLayout>(android.R.id.content)
            ?.viewTreeObserver?.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    @JvmStatic
    fun getNavigateBarHeight(activity: Activity?, callback: NavigationBarCallback) {
        val decorView = activity?.window?.decorView
        decorView?.isAttachedToWindow?.let { attachedToWindow ->
            if (attachedToWindow) {
                val windowInsets = ViewCompat.getRootWindowInsets(decorView)
                val height =
                    windowInsets!!.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                val hasNavigationBar =
                    windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                            && windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0
                if (height > 0) {
                    callback.onHeight(height, hasNavigationBar)
                } else {
                    callback.onHeight(getNavBarHeight(), hasNavigationBar)
                }
            } else {
                decorView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View) {
                        val windowInsets = ViewCompat.getRootWindowInsets(decorView)
                        val height =
                            windowInsets!!.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

                        val hasNavigationBar =
                            windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                                    && windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0

                        if (height > 0) {
                            callback.onHeight(height, hasNavigationBar)
                        } else {
                            callback.onHeight(getNavBarHeight(), hasNavigationBar)
                        }
                    }

                    override fun onViewDetachedFromWindow(v: View) {

                    }

                })
            }
        }

    }

    @SuppressLint("InternalInsetResource")
    @JvmStatic
    fun getNavBarHeight(): Int {
        val resources = Resources.getSystem()
        val identifier = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (identifier != 0) {
            resources.getDimensionPixelOffset(identifier)
        } else {
            0
        }
    }

    fun interface KeyboardHeightListener {
        fun onKeyboardHeightChanged(height: Int)
    }

    fun interface NavigationBarCallback {
        fun onHeight(height: Int, hasHeight: Boolean)
    }
}