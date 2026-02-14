package com.us.mauncview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.atan2
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

        // 转盘动画持续时间
        const val ANIM_DURATION = 4000L

        // 滑动轨迹记录的最大数量（越多计算越精准）
        private const val MAX_TRACK_POINTS = 10

        // 滑动轨迹记录的最小数量
        private const val MIN_TRACK_POINTS = 5

        // 最小触发惯性动画的角速度（°/ms），低于这个值不触发惯性
        private const val MIN_INERTIA_SPEED = 0.1f
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

    // 转盘指针方向
    private var pointerAngle = 270f

    // 每个扇形的绘制的起始角度
    private var startAngle = 0f

    // 计算出每个扇形的间隔
    private var sweepAngle = 0f

    // 扇形中间白线的大小
    private var sweepWhiteLineWidth = 1f

    // 第一绘制的扇形的颜色
    private var firstSectorColor = -1

    // 是否隐藏边框
    private var isHideBroad = false

    // 转盘内容的总长度
    private fun contentListSize() = turnTableContentList.size

    // 转盘转几圈
    private var turnMoveNumber = 5 * 360f

    // 转盘事件监听
    private var onTurnTableEventListener: OnTurnTableEventListener? = null

    // 转盘结束的结果
    private var resultPos = 0

    // 手指按下时的角度
    private var downAngle = 0f

    // 手指按下时转盘的初始角度
    private var downStartAngle = 0f

    // 是否正在触摸滑动
    private var isTouching = AtomicBoolean(false)

    // 是否正在执行动画
    private var isRotateTurnTable = AtomicBoolean(false)

    // 是否启用转盘触摸
    private var isEnableTouch = AtomicBoolean(false)

    // 是否因为页面生命周期暂停
    private var isLifecyclePause = AtomicBoolean(false)

    // 轨迹记录
    private var touchTrack = mutableListOf<Pair<Float, Long>>()

    // 背景的半径(比圆的半径大点)
    private var backGroundCircleRadius = 0f

    // 画圆形边框的笔
    private val turnTableStrokePaint: Paint by lazy {
        Paint().apply {
            color = Color.argb(200, 0, 0, 0)
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 21f
        }
    }

    // 画扇形的笔
    private val turnAnglePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
    }

    // 画白线的笔
    private val sectorDividePaint: Paint by lazy {
        Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
    }

    // 画字的笔
    private var textPaint: Paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        isDither = true
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private var colorResList = mutableListOf(
        Color.parseColor("#00E616"),
        Color.parseColor("#E70000"),
        Color.parseColor("#0083EB"),
    )

    private var turnTableContentList = mutableListOf(
        "", "", "", "", "", "", "", ""
    )

    // 转动动画
    private val rotateAnimator: ValueAnimator by lazy {
        ValueAnimator().apply { initAnimConfig(this) }
    }

    init {
        circleRadius = 450f
        backGroundCircleRadius = circleRadius + 10f
        sweepAngle = 360f / turnTableContentList.size
        firstSectorColor = colorResList[0]
    }

    /**
     * 两个动画的基础逻辑一致
     */
    private fun initAnimConfig(valueAnimator: ValueAnimator) {
        valueAnimator.setDuration(ANIM_DURATION)
        valueAnimator.interpolator = DecelerateInterpolator(1.5f)
        valueAnimator.addUpdateListener { animation ->
            // 控制旋转角度在0f到360f之间
            startAngle = animation.animatedValue as Float % 360
            val fromIndex = obtainCurrentAngleFromIndex()
            onTurnTableEventListener?.onRotateIng(turnTableContentList[fromIndex], fromIndex)
            invalidate()
        }
        valueAnimator.doOnStart {
            isRotateTurnTable.set(true)
            onTurnTableEventListener?.onRotateStart()
        }
        valueAnimator.doOnEnd {
            isRotateTurnTable.set(false)
            // cancel会回调，如果是因为触摸暂停的，不会触发转动结束
            if (isLifecyclePause.get()) {
                return@doOnEnd
            }
            if (!isTouching.get()) {
                onTurnTableEventListener?.onRotateEnd(turnTableContentList[resultPos])
            }
        }
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
        defaultSize: Int = 950,/*默认半径是450f 自适应长度=半径*2+黑色边框(30f)  半径可配置这里也可以配置*/
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnableTouch.get()) {
            return false
        }
        event ?: return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching.set(true)
                touchTrack.clear()
                pauseTurnTable()
                downAngle = calculateTouchAngle(event.x, event.y)
                downStartAngle = startAngle
                // 记录第一个轨迹点
                touchTrack.add(Pair(downAngle, System.currentTimeMillis()))
                Log.e(TAG, "eventDown:downAngle${downAngle},downStartAngle:${downStartAngle}")
            }

            MotionEvent.ACTION_MOVE -> {
                val currentAngle = calculateTouchAngle(event.x, event.y)
                // 记录滑动轨迹（保持最多MAX_TRACK_POINTS个点）
                touchTrack.add(Pair(currentAngle, System.currentTimeMillis()))
                if (touchTrack.size > MAX_TRACK_POINTS) {
                    touchTrack.removeFirst()
                }
                var angleDiff = currentAngle - downAngle
                if (angleDiff > 180) {
                    angleDiff -= 360
                } else if (angleDiff < -180) {
                    angleDiff += 360
                }
                startAngle = (downStartAngle - angleDiff + 360) % 360
                val fromIndex = obtainCurrentAngleFromIndex()
                onTurnTableEventListener?.onRotateIng(turnTableContentList[fromIndex], fromIndex)
                Log.e(TAG, "eventMove:startAngle:${startAngle},currentAngle:${currentAngle}")
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                calculateInertiaAndStartAnimation()
                downAngle = 0f
                downStartAngle = 0f
                isTouching.set(false)
                touchTrack.clear()
            }

            MotionEvent.ACTION_CANCEL -> {
                calculateInertiaAndStartAnimation()
                downAngle = 0f
                downStartAngle = 0f
                isTouching.set(false)
                touchTrack.clear()
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画边框
        if (!isHideBroad) {
            canvas.drawCircle(
                circleX, circleY, backGroundCircleRadius, turnTableStrokePaint
            )
        }
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
            canvas.drawSectorDivideLine()
            canvas.drawText(text)
            startAngle += sweepAngle
        }
    }

    private fun Canvas.drawSectorDivideLine() {
        val radian = Math.toRadians(startAngle.toDouble())
        // 分割线起点（外圆边缘）
        val startX = circleX + (circleRadius - sweepWhiteLineWidth) * cos(radian).toFloat()
        val startY = circleY + (circleRadius - sweepWhiteLineWidth) * sin(radian).toFloat()
        drawLine(startX, startY, circleX, circleY, sectorDividePaint)
    }

    private fun Canvas.drawText(text: String) {
        // 计算文本位置
        val textAngle = startAngle + sweepAngle / 2
        // 距离圆心的距离
        val textRadius = circleRadius * when (text.length) {
            1, 2, 3 -> 0.8f
            4, 5 -> 0.75f
            6 -> 0.7f
            else -> 0.7f
        }
        val radian = Math.toRadians(textAngle.toDouble())
        val textX = circleX + (textRadius * cos(radian)).toFloat()
        val textY = circleY + (textRadius * sin(radian)).toFloat()
        save()
        // 将画布原点平移到文本绘制的位置
        translate(textX, textY)
        // 旋转画布,让文本垂直+90f于扇形径向 ,如果你想让文本和扇形径向一致，直接用 textAngle 即可
        rotate(textAngle)
        // 绘制文本（此时原点已经平移到textX/textY，所以坐标填0,0）
        val fontMetrics = textPaint.fontMetrics
        val baseLineY = 0f - (fontMetrics.ascent + fontMetrics.descent) / 2
        drawText(text, 0f, baseLineY, textPaint)
        restore()
    }

    fun getTurnTableContents() = turnTableContentList

    fun setTurnTableContents(list: MutableList<String>) {
        turnTableContentList.clear()
        turnTableContentList.addAll(list)
        sweepAngle = 360f / turnTableContentList.size
        invalidate()
    }

    fun getTurnTableColor() = colorResList

    fun setTurnTableColor(list: MutableList<Int>) {
        colorResList.clear()
        colorResList.addAll(list)
        firstSectorColor = colorResList[0]
        invalidate()
    }

    /**
     * 转盘是否可触摸
     */
    fun setEnableTouch(isEnable: Boolean) {
        isEnableTouch.set(isEnable)
    }

    /**
     * 暂停动画
     */
    fun pauseTurnTable() {
        isRotateTurnTable.set(false)
        rotateAnimator.cancel()
    }

    /**
     * 设置半径
     */
    fun setCircleRadius(circleRadius: Float) {
        this.circleRadius = circleRadius
        backGroundCircleRadius = this.circleRadius + 10f
    }

    fun setHideBoard(isHide: Boolean) {
        this.isHideBroad = isHide
    }

    /**
     * 点击中心按钮启动转盘
     */
    fun startTurnTable() {
        if (isRotateTurnTable.get()) {
            Log.e(TAG, "turn table is rotate ing")
            return
        }
        obtainResultPos()
        // 计算转动到position位置停止后的角度值，指针是朝向正上方，正上放的角度是270
        val entAngle = pointerAngle - sweepAngle * (resultPos + angleOffset()) + turnMoveNumber
        startRotateAnim(startAngle, entAngle)
    }

    /**
     * 计算滑动角速度，并判断是否触发惯性动画
     */
    private fun calculateInertiaAndStartAnimation() {
        if (touchTrack.size < MIN_TRACK_POINTS) return // 轨迹点不足，不触发惯性
        val timeDelta = (touchTrack.last().second - touchTrack.first().second).toFloat()
        if (timeDelta == 0f) return // 时间差为0，避免除0
        // 遍历轨迹点，累加每一段的角度差（考虑360°循环），得到总滑动角度
        var totalAngleDelta = 0f
        for (i in 1 until touchTrack.size) {
            val prev = touchTrack[i - 1]
            val curr = touchTrack[i]
            totalAngleDelta += calculateDirectionalAngleDiff(prev.first, curr.first)
        }
        // 计算平均角速度（°/ms）：正数=逆时针，负数=顺时针
        val angularSpeed = totalAngleDelta / timeDelta
        // 低于阈值不触发惯性
        if (abs(angularSpeed) < MIN_INERTIA_SPEED) return
        Log.e(TAG, "滑动角速度：${angularSpeed} °/ms ")
        obtainResultPos()
        val targetSectorMiddleAngle = pointerAngle - (resultPos + angleOffset()) * sweepAngle
        // 计算当前角度到目标角度的带方向差值
        val diffToTarget = calculateDirectionalAngleDiff(startAngle, targetSectorMiddleAngle)
        // 计算惯性动画的目标角度：叠加旋转圈数（保持滑动方向）
        val inertiaTotalAngle = if (angularSpeed < 0) turnMoveNumber else -turnMoveNumber
        val entAngle = startAngle + inertiaTotalAngle + diffToTarget

        startRotateAnim(startAngle, entAngle)
    }

    /**
     * 启动转盘动画
     */
    private fun startRotateAnim(startValue: Float, endValue: Float) {
        rotateAnimator.cancel()
        rotateAnimator.setFloatValues(startValue, endValue)
        rotateAnimator.start()
    }

    /**
     * 判断当前指针角度对应的内容
     */
    private fun obtainCurrentAngleFromIndex(): Int {
        if (turnTableContentList.isEmpty()) return 0
        val currentStartAngle = normalizeAngle(startAngle)
        val effectiveSweepAngle = sweepAngle - sweepWhiteLineWidth
        // 角度精度容错（解决浮点数/绘制误差导致的边界误判）
        val angleTolerance = 0.5f //（0.1~1°）
        // 存储「扇形索引-角度差」，用于兜底找最近扇形
        val sectorAngleDiffMap = mutableMapOf<Int, Float>()
        for (index in turnTableContentList.indices) {
            // 计算当前扇形的有效起始/结束角度（含容错）
            val sectorStart = normalizeAngle(currentStartAngle + index * sweepAngle)
            val sectorEnd = normalizeAngle(sectorStart + effectiveSweepAngle + angleTolerance)
            // 用「角度差」判断是否在扇形区间
            val isInSector = if (sectorStart < sectorEnd) {
                // 普通区间（如30°~60°）：包含容错后的边界
                pointerAngle in (sectorStart - angleTolerance)..sectorEnd
            } else {
                // 跨360°区间（如350°~10°）：拆分判断+容错
                (pointerAngle >= sectorStart - angleTolerance) || (pointerAngle <= sectorEnd)
            }
            if (isInSector) {
                return index
            }
            // 未匹配时，计算指针到当前扇形中心的角度差（用于兜底）
            val sectorCenter = normalizeAngle(sectorStart + effectiveSweepAngle / 2)
            val angleDiff = calculateShortestAngleDiff(pointerAngle, sectorCenter)
            sectorAngleDiffMap[index] = angleDiff
        }
        // 落在白线时，返回「角度最近的扇形索引」
        return sectorAngleDiffMap.minByOrNull { it.value }?.key ?: 0
    }

    /**
     * 计算两个角度之间的「最短差值」（解决360°环形角度差问题）
     * 例如：350° 和 10° 的最短差值是 20°，而非 340°
     * 获取结果指针位置调用修正
     */
    private fun calculateShortestAngleDiff(angle1: Float, angle2: Float): Float {
        val diff = abs(angle1 - angle2)
        return if (diff > 180f) 360f - diff else diff
    }

    /**
     * 计算两个角度之间的带方向的最短差值（考虑360°循环）
     * 触摸旋转时调用修正
     */
    private fun calculateDirectionalAngleDiff(fromAngle: Float, toAngle: Float): Float {
        var diff = toAngle - fromAngle
        // 修正差值到 [-180, 180] 区间，保证是最短旋转路径
        if (diff > 180) diff -= 360 else if (diff < -180) diff += 360
        return diff
    }

    /**
     * 根据手指坐标判断当前角度
     */
    private fun calculateTouchAngle(x: Float, y: Float): Float {
        val dx = x - circleX
        val dy = y - circleY
        val radian = atan2(-dy.toDouble(), dx.toDouble())
        var angle = Math.toDegrees(radian).toFloat()
        if (angle < 0) {
            angle += 360f
        }
        return angle
    }

    /**
     * 转盘滚动终点随机停止的偏移量(只应用于点击按钮开始动画的时候)
     */
    private fun angleOffset(): Float {
        val num = Math.random().toFloat()
        return if (num > 0 && num < 1) num else 0.5.toFloat()
    }

    /**
     * 标准化角度到 0° ~ 360° 范围（消除负数角度）
     */
    private fun normalizeAngle(angle: Float): Float {
        val normalized = angle % 360f
        return if (normalized < 0) normalized + 360f else normalized
    }

    /**
     * 获取结果
     */
    private fun obtainResultPos() {
        resultPos = Random.nextInt(contentListSize())
    }

    /**
     * 处理最后一个颜色块防止合并
     */
    private fun obtainDrawColor(index: Int): Int {
        if (colorResList.isEmpty()) return obtainColorRes(R.color.turn_table_color_two)
        var drawColor = colorResList[index % colorResList.size]
        if (index == contentListSize() - 1 && drawColor == firstSectorColor) {
            drawColor = colorResList[(index + 1) % colorResList.size]
        }
        return drawColor
    }

    fun onPause() {
        isLifecyclePause.set(true)
        pauseTurnTable()
    }

    fun onResume() {
        isLifecyclePause.set(false)
    }

    interface OnTurnTableEventListener {
        fun onRotateStart()
        fun onRotateEnd(content: String)
        fun onRotateIng(content: String, posIndex: Int)
    }

    fun setOnTurnTableListener(onTurnTableEventListener: OnTurnTableEventListener) {
        this.onTurnTableEventListener = onTurnTableEventListener
    }

    private fun obtainColorRes(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
}