package com.us.mauncview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class TurnTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "TurnTableView"

        // 转盘动画减速因子
        const val DECELERATE_FACTOR = 2.0f

        // 转盘动画持续时间
        const val ANIM_DURATION = 5000L
    }

    // view的总宽度
    private var viewWidth = 0f

    // view的总高度
    private var viewHeight = 0f

    // 圆心坐标X
    private var circleX = 0f

    // 圆心坐标Y
    private var circleY = 0f

    // 圆的半径
    private var circleRadius = 0f

    // 每个扇形的绘制的起始角度
    private var startAngle = 0f

    // 计算出每个扇形的间隔
    private var sweepAngle = 0f

    // 画圆形背景的笔
    private val turnTableBackGroundPaint: Paint by lazy {
        Paint().apply {
            color = Color.GRAY
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = 8f
        }
    }

    // 画圆形边框的笔
    private val turnTableStrokePaint: Paint by lazy {
        Paint().apply {
            color = obtainColorRes(R.color.black)
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 30f
        }
    }

    // 画扇形的笔
    private val turnAnglePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    // 画字的笔
    private var textPaint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private var colorResList = mutableListOf(
        Color.parseColor("#FF0000"),
        Color.parseColor("#008EFF"),
        Color.parseColor("#FF3D00"),
        Color.parseColor("#FFE500"),
        Color.parseColor("#5C00FF"),
        Color.parseColor("#00FF0A")
    )

    private var turnTableContentList = mutableListOf(
        "昆仑镜", "女娲石", "神农鼎", "崆峒印", "伏羲琴", "万灵血珠"
    )

    // 动画
    private val valueAnimator: ValueAnimator by lazy {
        ValueAnimator().apply {
            setDuration(ANIM_DURATION)
            interpolator = DecelerateInterpolator(DECELERATE_FACTOR)
            addUpdateListener {
                val value: Float = it.animatedValue as Float
                // 控制sectorStartAngle在0到360之间
                startAngle = (value % 360 + 360) % 360
//                Log.e(TAG, "animatedValue:${startAngle}")
                invalidate()
            }
            doOnStart {
                Log.e(TAG, "doOnStart")
            }
            doOnEnd {
                Log.e(TAG, "doOnEnd")
            }
        }
    }

    init {
        circleRadius = 450f
        sweepAngle = 360f / turnTableContentList.size
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthModel = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightModel = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val finalWidthSize = calculateSize(widthModel, widthSize)
        val finalHeightSize = calculateSize(heightModel, heightSize)
        setMeasuredDimension(finalWidthSize, finalHeightSize)
    }

    /**
     * 根据MeasureSpec模式计算最终尺寸
     * @param measureMode 测量模式（EXACTLY/AT_MOST/UNSPECIFIED）
     * @param measureSize 测量尺寸
     */
    private fun calculateSize(
        measureMode: Int,
        measureSize: Int,
        defaultSize: Int = 200,
    ): Int {
        return when (measureMode) {
            // 精确模式：match_parent/固定数值，直接用父View给的size
            MeasureSpec.EXACTLY -> measureSize
            // 最大模式：wrap_content，用默认尺寸（但不超过父View给的size）
            MeasureSpec.AT_MOST -> min(defaultSize, measureSize)
            // 无约束模式：直接用默认尺寸
            MeasureSpec.UNSPECIFIED -> defaultSize
            else -> defaultSize
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e(TAG, "onSizeChanged  width:$w,height:${h}")
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        circleX = (w / 2).toFloat()
        circleY = (h / 2).toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画背景
        canvas.drawCircle(circleX, circleY, circleRadius, turnTableBackGroundPaint)
        // 画边框
        canvas.drawCircle(circleX, circleY, circleRadius, turnTableStrokePaint)
        // 画扇形 和 扇形上的文本
        turnTableContentList.forEachIndexed { index, text ->
            turnAnglePaint.color = colorResList[index]
            canvas.drawArc(
                circleX - circleRadius,
                circleY - circleRadius,
                circleX + circleRadius,
                circleY + circleRadius,
                startAngle, sweepAngle, true, turnAnglePaint
            )
            canvas.drawText(text)
            startAngle += sweepAngle
        }
    }

    private fun Canvas.drawText(text: String) {
        // 计算文本位置（和之前一致）
        val textAngle = startAngle + sweepAngle / 2
        // 距离圆心的距离
        val textRadius = circleRadius * 0.7f
        val radian = Math.toRadians(textAngle.toDouble())
        val textX = circleX + (textRadius * cos(radian)).toFloat()
        val textY = circleY + (textRadius * sin(radian)).toFloat()
        // 1. 保存当前画布状态
        save()
        // 2. 将画布原点平移到文本绘制的位置
        translate(textX, textY)
        // 3. 旋转画布,让文本垂直于扇形径向 ,如果你想让文本和扇形径向一致，直接用 textAngle 即可
        rotate(textAngle + 90f)
        // 4. 绘制文本（此时原点已经平移到textX/textY，所以坐标填0,0）
        val fontMetrics = textPaint.fontMetrics
        val baseLineY = 0f - (fontMetrics.ascent + fontMetrics.descent) / 2
        drawText(text, 0f, baseLineY, textPaint)
        // 5. 恢复画布状态（必须！否则后续绘制会继承旋转状态）
        restore()
    }

    fun setScrollToPos(pos: Int) {
        Log.e(TAG, "pos:$pos")
        val endAngle = sweepAngle * pos + 5 * 360
        Log.e(TAG, "startAngle:$startAngle,endAngle:$endAngle")
        valueAnimator.setFloatValues(startAngle, endAngle)
        valueAnimator.start()
    }

    private fun obtainColorRes(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
}