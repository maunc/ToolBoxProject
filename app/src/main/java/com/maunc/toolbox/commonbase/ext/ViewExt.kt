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
import androidx.annotation.DrawableRes
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
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
    visibility = if (flag) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    val toolContext = ToolBoxApplication.app
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        imageTintList = ContextCompat.getColorStateList(toolContext, colorRes)
        imageTintMode = PorterDuff.Mode.SRC_IN
    } else {
        setColorFilter(ContextCompat.getColor(toolContext, colorRes))
    }
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
    time: Long = 100,
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

/**
 * 设置宽度和高度，带有过渡动画
 */
fun View.animateSetWidthAndHeight(
    targetWidth: Int = 100,
    targetHeight: Int = 100,
    duration: Long = 70,
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

/**
 * 设置View的宽度和高度
 */
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
