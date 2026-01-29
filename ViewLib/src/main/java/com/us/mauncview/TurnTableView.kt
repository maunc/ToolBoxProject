package com.us.mauncview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
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
    }

    // view的总宽度
    private var viewWidth = 0f

    // view的总高度
    private var viewHeight = 0f

    // 圆心坐标X
    private var centerCircleX = 0f

    // 圆心坐标Y
    private var centerCircleY = 0f

    // 圆的半径
    private var circleRadius: Float = 0f

    // 每个扇形的绘制的起始角度
    private var sectorStartAngle = 0f

    // 每个扇形的绘制的结束角度
    private var sectorSweepAngle = 0f

    // 画圆形背景的笔
    private var turnTableBackGroundPaint: Paint = Paint().apply {
        color = Color.GRAY
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeWidth = 8f
    }

    // 画圆形边框的笔
    private var turnTableStrokePaint: Paint = Paint().apply {
        color = obtainColorRes(R.color.black)
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 30f
    }

    private var colorResList = mutableListOf(
        R.color.red,
        R.color.blue,
        R.color.orange,
        R.color.purple,
        R.color.yellow,
        R.color.green
    )

    private var turnTableContentList = mutableListOf(
        "蒙奇D路飞", "柯南", "漩涡鸣人", "黑崎一护", "娜美", "青雉"
    )

    // 画扇形的笔
    private var turnTableSectorPaints: MutableList<Paint> = mutableListOf()

    init {
        circleRadius = 450f
        colorResList.forEach { colorRes ->
            turnTableSectorPaints.add(Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = obtainColorRes(colorRes)
            })
        }
    }

    // 画字的笔
    private var testPaint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        textSize = 36f
        textAlign = Paint.Align.CENTER
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
            // 默认情况
            else -> defaultSize
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e(TAG, "onSizeChanged  width:$w,height:${h}")
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        centerCircleX = (w / 2).toFloat()
        centerCircleY = (h / 2).toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画背景
        canvas.drawCircle(centerCircleX, centerCircleY, circleRadius, turnTableBackGroundPaint)
        // 画边框
        canvas.drawCircle(centerCircleX, centerCircleY, circleRadius, turnTableStrokePaint)
        // 计算出每个扇形的间隔
        val sectorSweepAngle = 360f / turnTableSectorPaints.size
        turnTableSectorPaints.forEachIndexed { index, paint ->
            canvas.drawArc(
                centerCircleX - circleRadius,
                centerCircleY - circleRadius,
                centerCircleX + circleRadius,
                centerCircleY + circleRadius,
                sectorStartAngle, sectorSweepAngle, true, paint
            )
            drawText(
                canvas,
                sectorStartAngle,
                sectorSweepAngle,
                circleRadius.toInt(),
                turnTableContentList[index],
                testPaint,
            )
            sectorStartAngle = sectorSweepAngle * (index + 1)
        }

        // 画中心圆
    }

    //绘制文字
    private fun drawText(
        canvas: Canvas,
        startAngle: Float,
        sweepAngle: Float,
        radius: Int,
        string: String,
        textPaint: Paint,
    ) {
        // 计算文本位置（和之前一致）
        val textAngle = startAngle + sweepAngle / 2
        val textRadius = radius * 0.7f
        val radian = Math.toRadians(textAngle.toDouble())
        val textX = centerCircleX + (textRadius * cos(radian)).toFloat()
        val textY = centerCircleY + (textRadius * sin(radian)).toFloat()
        // 1. 保存当前画布状态
        canvas.save()
        // 2. 将画布原点平移到文本绘制的位置
        canvas.translate(textX, textY)
        // 3. 旋转画布,让文本垂直于扇形径向 ,如果你想让文本和扇形径向一致，直接用 textAngle 即可
        canvas.rotate(textAngle + 90f)
        // 4. 绘制文本（此时原点已经平移到textX/textY，所以坐标填0,0）
        val fontMetrics = textPaint.fontMetrics
        val baseLineY = 0f - (fontMetrics.ascent + fontMetrics.descent) / 2
        canvas.drawText(string, 0f, baseLineY, textPaint)
        // 5. 恢复画布状态（必须！否则后续绘制会继承旋转状态）
        canvas.restore()
    }

    private fun obtainColorRes(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
}