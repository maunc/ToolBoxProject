package com.maunc.toolbox.commonbase.ext

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.animation.BaseInterpolator
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.maunc.toolbox.ToolBoxApplication
import com.maunc.toolbox.commonbase.constant.ALPHA
import com.maunc.toolbox.commonbase.constant.SCALE_X
import com.maunc.toolbox.commonbase.constant.SCALE_Y

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visibleOrGone(flag: Boolean) {
    visibility = if (flag) View.VISIBLE else View.GONE
}

fun View.obtainViewWidth(action: (Int) -> Unit) {
    post { action.invoke(width) }
}

fun View.obtainViewHeight(action: (Int) -> Unit) {
    post { action.invoke(height) }
}

@SuppressLint("ClickableViewAccessibility")
fun View.addGestureDetector(
    onDown: (MotionEvent) -> Unit = {},
    onShowPress: (MotionEvent) -> Unit = {},
    onSingleTapUp: (MotionEvent) -> Unit = {},
    onScroll: (MotionEvent?, MotionEvent, Float, Float) -> Unit = { _, _, _, _ -> },
    onLongPress: (MotionEvent) -> Unit = {},
    onFling: (MotionEvent?, MotionEvent, Float, Float) -> Unit = { _, _, _, _ -> },
    onSingleTapConfirmed: (MotionEvent) -> Unit = {},
    onDoubleTap: (MotionEvent) -> Unit = {},
    onDoubleTapEvent: (MotionEvent) -> Unit = {},
) {
    val gestureDetector = GestureDetector(this.context, object : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            // 手指按下时触发
            onDown.invoke(e)
            return true
        }

        override fun onShowPress(e: MotionEvent) {
            // 手指按下一段时间，但还未松开或拖动时触发
            onShowPress.invoke(e)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // 手指轻点抬起时触发
            onSingleTapUp.invoke(e)
            return true
        }

        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent,
            distanceX: Float, distanceY: Float,
        ): Boolean {
            // 手指拖动时触发
            onScroll.invoke(e1, e2, distanceX, distanceY)
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            // 手指长按屏幕时触发
            onLongPress.invoke(e)
        }

        override fun onFling(
            e1: MotionEvent?, e2: MotionEvent,
            velocityX: Float, velocityY: Float,
        ): Boolean {
            // 手指快速滑动并松开时触发
            onFling.invoke(e1, e2, velocityX, velocityY)
            return true
        }
    })
    gestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            // 确认是单次点击时触发
            onSingleTapConfirmed.invoke(e)
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // 检测到双击时触发
            onDoubleTap.invoke(e)
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            // 双击过程中的事件，如按下、移动、抬起
            onDoubleTapEvent.invoke(e)
            return true
        }

    })
    this.setOnTouchListener { v, event ->
        return@setOnTouchListener gestureDetector.onTouchEvent(event)
    }
}

/**
 * 设置ImageView底色
 */
fun ImageView.setTint(@ColorRes colorRes: Int) {
    val toolContext = ToolBoxApplication.app
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        imageTintList = ContextCompat.getColorStateList(toolContext, colorRes)
        imageTintMode = PorterDuff.Mode.SRC_IN
    } else {
        setColorFilter(ContextCompat.getColor(toolContext, colorRes))
    }
}

/**
 * 设置跑马灯
 */
fun TextView.marquee() {
    ellipsize = TextUtils.TruncateAt.MARQUEE
    marqueeRepeatLimit = -1
    isSingleLine = true
    isFocusable = true
    isSelected = true
    isFocusableInTouchMode = true
}

/**
 * 输入框获取焦点
 */
fun EditText.requestFocusable() {
    isSelected = true
    isEnabled = true
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
}

/**=========================================View  Listener  优化=========================================*/
fun ViewPager.addViewPageListener(
    onPageScrolled: (Int, Float, Int) -> Unit = { _, _, _ -> },
    onPageSelected: (Int) -> Unit = {},
    onPageScrollStateChanged: (Int) -> Unit = {},
) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }
    })
}

fun EditText.addEditTextListener(
    afterTextChanged: (String) -> Unit = {},
    beforeTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { _, _, _, _ -> },
    onTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { _, _, _, _ -> },
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s, start, count, count)
        }
    })
}

fun DrawerLayout.addDrawLayoutListener(
    onDrawerSlide: (View, Float) -> Unit = { _, _ -> },
    onDrawerOpened: (View) -> Unit = {},
    onDrawerClosed: (View) -> Unit = {},
    onDrawerStateChanged: (Int) -> Unit = {},
) {
    this.addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            onDrawerSlide.invoke(drawerView, slideOffset)
        }

        override fun onDrawerOpened(drawerView: View) {
            onDrawerOpened.invoke(drawerView)
        }

        override fun onDrawerClosed(drawerView: View) {
            onDrawerClosed.invoke(drawerView)
        }

        override fun onDrawerStateChanged(newState: Int) {
            onDrawerStateChanged.invoke(newState)
        }
    })
}

fun SeekBar.addSeekBarListener(
    onProgressChanged: (SeekBar?, Int, Boolean) -> Unit = { _, _, _ -> },
    onStartTrackingTouch: (SeekBar?) -> Unit = {},
    onStopTrackingTouch: (SeekBar?) -> Unit = {},
) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChanged.invoke(seekBar, progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            onStartTrackingTouch.invoke(seekBar)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            onStopTrackingTouch.invoke(seekBar)
        }
    })
}

fun RecyclerView.addRecyclerViewScrollListener(
    onScrollStateChanged: (RecyclerView, Int) -> Unit = { _, _ -> },
    onScrolled: (RecyclerView, Int, Int) -> Unit = { _, _, _ -> },
) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            onScrollStateChanged.invoke(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onScrolled.invoke(recyclerView, dx, dy)
        }
    })
}
/**===================================================================================================*/

/**
 * 防止重复点击事件 默认0.1秒内不可重复点击
 */
var lastClickTime = 0L
fun View.clickNoRepeat(
    interval: Long = 100,
    action: (view: View) -> Unit,
) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}

/**
 * 点击缩放动画点击
 */
@SuppressLint("ClickableViewAccessibility", "Recycle")
fun View.clickScale(
    scaleMax: Float = 1f,
    scaleMin: Float = 0.9f,
    scaleTransit: Float = 1.1f,
    scaleDuration: Long = 60,
    scaleInterpolator: BaseInterpolator = LinearInterpolator(),
    action: (view: View) -> Unit,
) {
    //缩小动画
    val scaleOutXValueHolder: PropertyValuesHolder =
        PropertyValuesHolder.ofFloat(SCALE_X, scaleMax, scaleMin)
    val scaleOutYValueHolder: PropertyValuesHolder =
        PropertyValuesHolder.ofFloat(SCALE_Y, scaleMax, scaleMin)
    val scaleOutAnim: Animator = ObjectAnimator.ofPropertyValuesHolder(
        this, scaleOutXValueHolder, scaleOutYValueHolder
    ).apply {
        duration = scaleDuration
        interpolator = scaleInterpolator
    }
    //扩大动画
    val scaleInXValueHolder: PropertyValuesHolder =
        PropertyValuesHolder.ofFloat(SCALE_X, scaleMin, scaleMax, scaleTransit, scaleMax)
    val scaleInYValueHolder: PropertyValuesHolder =
        PropertyValuesHolder.ofFloat(SCALE_Y, scaleMin, scaleMax, scaleTransit, scaleMax)
    val scaleInAnim: Animator = ObjectAnimator.ofPropertyValuesHolder(
        this, scaleInXValueHolder, scaleInYValueHolder,
    ).apply {
        duration = scaleDuration
        interpolator = scaleInterpolator
    }
    setOnTouchListener { view, event ->
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                post {
                    //配合drawable文件使用
                    isPressed = true
                    scaleOutAnim.start()
                }
            }

            MotionEvent.ACTION_UP -> {
                post {
                    isPressed = false
                    scaleOutAnim.end()
                    scaleInAnim.start()
                }
                if (isTouchPointInView(event.rawX.toInt(), event.rawY.toInt())) {
                    action(this)
                }
            }
        }
        return@setOnTouchListener true
    }
}

fun View.animateToAlpha(
    startAlpha: Float = 0f,
    endAlpha: Float = 1f,
    time: Long = 50,
    overHide: Boolean = false,
    onAnimEnd: (View) -> Unit = {},
) {
    ObjectAnimator.ofFloat(this, ALPHA, startAlpha, endAlpha).apply {
        duration = time
        addListener(onEnd = {
            visibleOrGone(!overHide)
            onAnimEnd(this@animateToAlpha)
        })
    }.start()
}

fun View.animateSetWidth(
    targetValue: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null,
) {
    post {
        ValueAnimator.ofInt(width, targetValue).apply {
            addUpdateListener {
                setWidth(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

fun View.animateSetHeight(
    targetValue: Int,
    duration: Long = 100,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null,
) {
    post {
        ValueAnimator.ofInt(height, targetValue).apply {
            addUpdateListener {
                setHeight(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

fun View.animateSetWidthAndHeight(
    targetWidth: Int = 100,
    targetHeight: Int = 100,
    duration: Long = 50,
    action: ((Float) -> Unit)? = null,
    endListener: () -> Unit = {},
) {
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        ValueAnimator.ofInt(width, targetWidth).apply {
            addUpdateListener {
                setWidthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, targetHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            addListener(onEnd = {
                endListener.invoke()
            })
            setDuration(duration)
            start()
        }
    }
}

fun View.setWidth(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

fun View.setHeight(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

fun View.setWidthAndHeight(
    width: Int,
    height: Int,
): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    params.width = width
    params.height = height
    layoutParams = params
    return this
}

/**
 * 检测当前手势是否在View范围内
 */
fun View.isTouchPointInView(x: Int, y: Int): Boolean {
    val location = IntArray(2)
    getLocationOnScreen(location)
    val left = location[0]
    val top = location[1]
    val right = left + measuredWidth
    val bottom = top + measuredHeight
    return y in top..bottom && x in left..right
}

/**
 * 获取View所在屏幕的位置
 */
fun View.obtainLocationWithScreen(
    resultCallback: (IntArray) -> Unit,
) {
    val locations = IntArray(2)
    post {
        this.getLocationOnScreen(locations)
        resultCallback.invoke(locations)
    }
}

/**
 * 扩大点击区域
 */
fun View.expandTouchArea(size: Int) {
    val parentView = this.parent as View
    parentView.post {
        val rect = Rect()
        this.getHitRect(rect)
        rect.top -= size
        rect.bottom += size
        rect.left -= size
        rect.right += size
        parentView.touchDelegate = TouchDelegate(rect, this)
    }
}
