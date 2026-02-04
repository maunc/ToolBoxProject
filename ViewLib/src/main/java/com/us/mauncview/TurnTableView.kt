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
import kotlin.random.Random

class TurnTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "TurnTableView"

        // 转盘动画减速因子
        const val DECELERATE_FACTOR = 3.0f

        // 转盘动画持续时间
        const val ANIM_DURATION = 3500L
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

    // 第一绘制的扇形的颜色
    private var firstSectorColor = -1

    // 转盘内容的总长度
    private fun contentListSize() = turnTableContentList.size

    // 转盘转几圈
    private var turnMoveNumber = 5 * 360f

    // 转动结束的后的真实角度
    private var endRealAngle = 0f

    // 是否正在是用转盘
    private var isRotateTurnTable = false

    // 转盘时间监听
    private var onTurnTableEventListener: OnTurnTableEventListener? = null

    // 转盘的结果
    private var resultPos = 0

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
        obtainColorRes(R.color.turn_table_color_one),
        obtainColorRes(R.color.turn_table_color_two),
        obtainColorRes(R.color.turn_table_color_three)
    )

    private var turnTableContentList = mutableListOf(
        "昆仑镜", "女娲石", "神农鼎", "崆峒印", "伏羲琴", "万灵血珠"
    )

    // 转动动画
    private val valueAnimator: ValueAnimator by lazy {
        ValueAnimator().apply {
            setDuration(ANIM_DURATION)
            interpolator = DecelerateInterpolator(DECELERATE_FACTOR)
            addUpdateListener {
                val value = animatedValue as Float
                // 控制旋转角度在0f到360f之间
                startAngle = (value % 360 + 360) % 360
                invalidate()
            }
            doOnStart {
                isRotateTurnTable = true
                onTurnTableEventListener?.onRotateStart()
            }
            doOnEnd {
                isRotateTurnTable = false
                onTurnTableEventListener?.onRotateEnd(turnTableContentList[resultPos])
            }
        }
    }

    init {
        circleRadius = 450f
        sweepAngle = 360f / turnTableContentList.size
        firstSectorColor = colorResList[0]
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
        Log.e(TAG, "onSizeChanged  width:$w,height:$h")
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
            turnAnglePaint.color = obtainDrawColor(index)
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

    fun setContentList(list: MutableList<String>) {
        turnTableContentList.clear()
        turnTableContentList.addAll(list)
        sweepAngle = 360f / turnTableContentList.size
        invalidate()
    }

    /**
     * 启动转盘
     */
    fun startMoveTurnTable() {
        if (isRotateTurnTable) {
            Log.e(TAG, "turn table is rotate ing")
            return
        }
        resultPos = Random.nextInt(contentListSize())
        //计算转动到position位置停止后的角度值
        val ran = getRandomPositionPro()
        val entAngle = 270 - sweepAngle * (resultPos.toFloat() + ran) + turnMoveNumber
        valueAnimator.setFloatValues(startAngle, entAngle)
        valueAnimator.start()
    }

    /**
     * 转盘滚动终点随机停止的位置
     */
    private fun getRandomPositionPro(): Float {
        val num = Math.random().toFloat()
        return if (num > 0 && num < 1) num else 0.5.toFloat()
    }

    /**
     * 处理最后一个颜色块防止合并
     */
    private fun obtainDrawColor(index: Int): Int {
        var drawColor = colorResList[index % colorResList.size]
        if (index == contentListSize() - 1 && drawColor == firstSectorColor) {
            drawColor = colorResList[(index + 1) % colorResList.size]
        }
        return drawColor
    }

    interface OnTurnTableEventListener {
        fun onRotateStart()
        fun onRotateEnd(content: String)
    }

    fun setOnTurnTableListener(onTurnTableEventListener: OnTurnTableEventListener) {
        this.onTurnTableEventListener = onTurnTableEventListener
    }

    private fun obtainColorRes(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
}