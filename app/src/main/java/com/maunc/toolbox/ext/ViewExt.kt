package com.maunc.toolbox.ext

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.BaseInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.EditText
import android.widget.TextView
import com.maunc.toolbox.constant.SCALE_X
import com.maunc.toolbox.constant.SCALE_Y

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

/**
 * 优化输入框
 */
fun EditText.afterTextChange(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}

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

/**
 * 旋转动画
 */
val rotateAnimation = RotateAnimation(
    0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
    RotateAnimation.RELATIVE_TO_SELF, 0.5f
).apply {
    duration = 500L
    repeatCount = -1
}

fun View.startRotation() {
    startAnimation(rotateAnimation)
}

fun View.stopRotation() {
    clearAnimation()
}

/**
 * 设置宽度和高度，带有过渡动画
 */
fun View.animateSetWidthAndHeight(
    targetWidth: Int,
    targetHeight: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null,
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
            if (listener != null) addListener(listener)
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
        ViewGroup.LayoutParams.WRAP_CONTENT
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