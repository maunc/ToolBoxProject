package com.maunc.toolbox.pushbox.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import com.maunc.toolbox.R
import com.maunc.base.ext.obtainDrawable
import com.maunc.base.ext.obtainScreenHeight
import com.maunc.base.ext.obtainScreenWidth
import com.maunc.toolbox.pushbox.constant.BOX
import com.maunc.toolbox.pushbox.constant.BOX_AT_GOAL
import com.maunc.toolbox.pushbox.constant.GOAL
import com.maunc.toolbox.pushbox.constant.MAN
import com.maunc.toolbox.pushbox.constant.MAN_AT_GOAL
import com.maunc.toolbox.pushbox.constant.PushBoxMoveDirection
import com.maunc.toolbox.pushbox.constant.ROAD
import com.maunc.toolbox.pushbox.constant.WALL
import com.maunc.toolbox.pushbox.constant.allGradesMapData
import com.maunc.toolbox.pushbox.constant.obtainTargetMap
import com.maunc.toolbox.pushbox.constant.obtainTargetPureOriginalMap
import com.maunc.toolbox.pushbox.data.PushBoxStepRecord
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@SuppressLint("NotConstructor")
class PushBoxGameView(
    context: Context?,
    attrs: AttributeSet?,
) : View(context, attrs) {

    companion object {
        private const val MIN_SLIDE_DISTANCE = 30f // 最小滑动距离（过滤误触）
    }

    private var screenWidth = obtainScreenWidth() // 屏幕宽
    private var screenHeight = obtainScreenHeight() // 屏幕高
    private var currentGradleIndex = 0 // 当前关数
    private lateinit var currentMap: Array<IntArray>// 当前地图(随着人物变化而变化)
    private lateinit var originalMap: Array<IntArray> // 当前地图的原始地图
    private var currentMapRow = 0 // 当前地图行数
    private var currentMapColumn = 0 // 当前地图列数
    private var manLocationX = 0 // 人所在行
    private var manLocationY = 0 // 人所在列
    private var currentGradleMoveNumber = 0 //当前关卡走过的步数

    private var xoff = 30f // 左边距
    private var yoff = 60f // 上边距
    private lateinit var picList: Array<Bitmap?> // 图片
    private var currentPicSize = 0 // 图片大小
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }
    private var isAnimating = false // 是否正在播放移动动画
    private var isPushBox = false // 是否是推箱子移动
    private var currentMoveDir: PushBoxMoveDirection? = null // 当前移动方向
    private var moveAnimator: ValueAnimator? = null // 移动动画对象
    private var animStartManX = 0 // 动画起始时人物X坐标
    private var animStartManY = 0 // 动画起始时人物Y坐标
    private var animOffsetX = 0f // X轴动画偏移量
    private var animOffsetY = 0f // Y轴动画偏移量
    private val stepHistory = mutableListOf<PushBoxStepRecord>() // 步数记录列表
    private var canUndo = false // 是否可以回退

    //测量大小
    private var measuredViewWidth = 0
    private var measuredViewHeight = 0

    private var touchStartX = 0f // 触摸起点X
    private var touchStartY = 0f // 触摸起点Y

    // 是否启用触摸移动
    private var isEnableTouchMove = AtomicBoolean(false)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val defaultWidth = (screenWidth * 0.55f).toInt()
        val defaultHeight = (screenHeight * 0.55f).toInt()
        measuredViewWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(defaultWidth, widthSize)
            else -> defaultWidth
        }
        measuredViewHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(defaultHeight, heightSize)
            else -> defaultHeight
        }
        setMeasuredDimension(measuredViewWidth, measuredViewHeight)
        initMap()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            measuredViewWidth = w
            measuredViewHeight = h
            initMap()
            invalidate()
        }
    }

    /**
     * 手动设置关卡
     */
    fun setGateIndex(index: Int, error: (String) -> Unit = {}) {
        if (index > allGradesMapData.size - 1) {
            error.invoke("已经没有关卡了")
            return
        }
        this.currentGradleIndex = index
        clearStepHistory()
        initMap()
        invalidate()
    }

    /**
     * 记录移动前的状态
     */
    private fun recordStepBeforeMove(direction: PushBoxMoveDirection, isPushBox: Boolean) {
        // 深拷贝当前地图状态（避免引用传递）
        val mapCopy = Array(currentMapRow) { i ->
            currentMap[i].copyOf()
        }
        stepHistory.add(
            PushBoxStepRecord(
                mapState = mapCopy,
                manX = manLocationX,
                manY = manLocationY,
                moveDirection = direction,
                isPushBox = isPushBox
            )
        )
        canUndo = stepHistory.isNotEmpty()
    }

    /**
     * 回退上一步操作
     */
    fun undoLastStep(): Boolean {
        if (!canUndo || stepHistory.isEmpty() || isAnimating) return false
        val lastStep = stepHistory[stepHistory.size - 1]
        stepHistory.removeAt(stepHistory.size - 1)
        canUndo = stepHistory.isNotEmpty()

        currentMap = lastStep.mapState
        manLocationX = lastStep.manX
        manLocationY = lastStep.manY
        if (currentGradleMoveNumber > 0) {
            currentGradleMoveNumber--
            onPushBoxEventListener?.onCurrentGradeMoveNumber(currentGradleMoveNumber)
        }
        invalidate()
        return true
    }

    private fun clearStepHistory() {
        stepHistory.clear()
        canUndo = false
        currentGradleMoveNumber = 0
    }

    fun canUndoStep() = canUndo && !isAnimating && stepHistory.isNotEmpty()

    fun obtainCurrentGradleIndex() = Pair(currentGradleIndex, currentMap)

    /**
     * 初始化地图
     */
    private fun initMap() {
        currentMap = obtainTargetMap(currentGradleIndex)
        originalMap = obtainTargetPureOriginalMap(currentGradleIndex)

        /**初始化当前地图的信息*/
        currentMapRow = currentMap.size
        currentMapColumn = if (currentMapRow > 0) currentMap[0].size else 0

        val maxSide = max(currentMapRow, currentMapColumn)

        val availableWidth = screenWidth - 2 * xoff - 20
        val availableHeight = screenHeight - yoff - 40
        val s1 = floor((availableWidth / maxSide).toDouble()).toInt()
        val s2 = floor((availableHeight / maxSide).toDouble()).toInt()
        currentPicSize = min(s1, s2)
        currentPicSize = max(currentPicSize, 40)
        //获取人物位置
        for (i in currentMap.indices) {
            for (j in currentMap[0].indices) {
                if (currentMap[i][j] == MAN || currentMap[i][j] == MAN_AT_GOAL) {
                    manLocationX = i
                    manLocationY = j
                    break
                }
            }
        }
        //重置图片大小
        initPic()
    }

    //初始化图片资源
    private fun initPic() {
        picList = arrayOfNulls(8)
        obtainDrawable(R.drawable.icon_push_box_qiang)?.let { loadPic(WALL, it) }
        obtainDrawable(R.drawable.icon_push_box_goal)?.let { loadPic(GOAL, it) }
        obtainDrawable(R.drawable.icon_push_box_lu)?.let { loadPic(ROAD, it) }
        obtainDrawable(R.drawable.icon_push_box_xiangzi)?.let { loadPic(BOX, it) }
        obtainDrawable(R.drawable.icon_push_box_boxgoal)?.let { loadPic(BOX_AT_GOAL, it) }
        obtainDrawable(R.drawable.icon_push_box_ren)?.let { loadPic(MAN, it) }
        obtainDrawable(R.drawable.icon_push_box_rengoal)?.let { loadPic(MAN_AT_GOAL, it) }
    }

    //加载图片
    private fun loadPic(key: Int, tile: Drawable) {
        val bitmap = Bitmap.createBitmap(currentPicSize, currentPicSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        tile.setBounds(0, 0, currentPicSize, currentPicSize)
        tile.draw(canvas)
        picList[key] = bitmap
    }

    /**
     * 检验游戏是否结束
     */
    private fun verifyGameFinished(): Boolean {
        var finish = true
        for (i in 0 until currentMapRow) {
            for (j in 0 until currentMapColumn) {
                if (currentMap[i][j] == GOAL || currentMap[i][j] == BOX) {
                    finish = false
                }
            }
        }
        return finish
    }

    fun setTouchMove(isTouch: Boolean) = isEnableTouchMove.set(isTouch)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isAnimating) return true
        if (!isEnableTouchMove.get()) return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                return true
            }

            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                val dx = endX - touchStartX // X轴偏移（正：右滑，负：左滑）
                val dy = endY - touchStartY // Y轴偏移（正：下滑，负：上滑）

                val absDx = abs(dx)
                val absDy = abs(dy)

                // 过滤无效滑动（偏移量小于最小距离）
                if (absDx < MIN_SLIDE_DISTANCE && absDy < MIN_SLIDE_DISTANCE) {
                    return true
                }
                if (absDx > absDy) { // 横向滑动
                    if (dx > 0) moveRight() else moveLeft()
                } else {  // 纵向滑动
                    if (dy > 0) moveDown() else moveUp()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    // 移动方法（上下左右）
    fun moveRight() = move(PushBoxMoveDirection.RIGHT)
    fun moveLeft() = move(PushBoxMoveDirection.LEFT)
    fun moveUp() = move(PushBoxMoveDirection.UP)
    fun moveDown() = move(PushBoxMoveDirection.DOWN)

    /**
     * 统一移动逻辑（减少重复代码）
     */
    private fun move(direction: PushBoxMoveDirection) {
        if (isAnimating || moveAnimator?.isRunning == true) return
        val (canMove, needPushBox) = checkMoveCondition(direction)
        if (!canMove) return
        recordStepBeforeMove(direction, needPushBox)
        startMoveAnimation(direction, needPushBox)
    }

    /**
     * 统一检查移动条件
     */
    private fun checkMoveCondition(direction: PushBoxMoveDirection): Pair<Boolean, Boolean> {
        return when (direction) {
            PushBoxMoveDirection.RIGHT -> {
                val nextY = manLocationY + 1
                if (nextY >= currentMapColumn) Pair(false, false)
                val needPushBox =
                    currentMap[manLocationX][nextY] == BOX || currentMap[manLocationX][nextY] == BOX_AT_GOAL
                val canMove = if (needPushBox) {
                    val boxNextY = nextY + 1
                    // 箱子的下一个位置也要先判边界
                    boxNextY < currentMapColumn &&
                            (currentMap[manLocationX][boxNextY] == GOAL || currentMap[manLocationX][boxNextY] == ROAD)
                } else {
                    // 无箱子时，边界已在上文判断过，直接返回true
                    currentMap[manLocationX][nextY] == GOAL || currentMap[manLocationX][nextY] == ROAD
                }
                Pair(canMove, needPushBox)
            }

            PushBoxMoveDirection.LEFT -> {
                val nextY = manLocationY - 1
                if (nextY < 0) Pair(false, false)
                val needPushBox =
                    currentMap[manLocationX][nextY] == BOX || currentMap[manLocationX][nextY] == BOX_AT_GOAL
                val canMove = if (needPushBox) {
                    val boxNextY = nextY - 1
                    boxNextY >= 0 &&
                            (currentMap[manLocationX][boxNextY] == GOAL || currentMap[manLocationX][boxNextY] == ROAD)
                } else {
                    currentMap[manLocationX][nextY] == GOAL || currentMap[manLocationX][nextY] == ROAD
                }
                Pair(canMove, needPushBox)
            }

            PushBoxMoveDirection.UP -> {
                val nextX = manLocationX - 1
                if (nextX < 0) Pair(false, false)
                val needPushBox =
                    currentMap[nextX][manLocationY] == BOX || currentMap[nextX][manLocationY] == BOX_AT_GOAL
                val canMove = if (needPushBox) {
                    val boxNextX = nextX - 1
                    boxNextX >= 0 &&
                            (currentMap[boxNextX][manLocationY] == GOAL || currentMap[boxNextX][manLocationY] == ROAD)
                } else {
                    // 边界已在上文判断，直接判断地形
                    currentMap[nextX][manLocationY] == GOAL || currentMap[nextX][manLocationY] == ROAD
                }
                Pair(canMove, needPushBox)
            }

            PushBoxMoveDirection.DOWN -> {
                val nextX = manLocationX + 1
                if (nextX >= currentMapRow) Pair(false, false)
                val needPushBox =
                    currentMap[nextX][manLocationY] == BOX || currentMap[nextX][manLocationY] == BOX_AT_GOAL
                val canMove = if (needPushBox) {
                    val boxNextX = nextX + 1
                    boxNextX < currentMapRow &&
                            (currentMap[boxNextX][manLocationY] == GOAL || currentMap[boxNextX][manLocationY] == ROAD)
                } else {
                    currentMap[nextX][manLocationY] == GOAL || currentMap[nextX][manLocationY] == ROAD
                }
                Pair(canMove, needPushBox)
            }
        }
    }

    /**
     * 启动移动动画
     * @param moveDirection 移动方向
     * @param isPushBox 是否推箱子
     */
    private fun startMoveAnimation(moveDirection: PushBoxMoveDirection, isPushBox: Boolean) {
        this.isAnimating = true
        this.currentMoveDir = moveDirection
        this.isPushBox = isPushBox
        animOffsetX = 0f
        animOffsetY = 0f
        // 记录动画起始时的人物坐标
        animStartManX = manLocationX
        animStartManY = manLocationY

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 66
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            when (moveDirection) {
                PushBoxMoveDirection.RIGHT -> {
                    animOffsetX = currentPicSize * progress
                    animOffsetY = 0f
                }

                PushBoxMoveDirection.LEFT -> {
                    animOffsetX = -currentPicSize * progress
                    animOffsetY = 0f
                }

                PushBoxMoveDirection.UP -> {
                    animOffsetX = 0f
                    animOffsetY = -currentPicSize * progress
                }

                PushBoxMoveDirection.DOWN -> {
                    animOffsetX = 0f
                    animOffsetY = currentPicSize * progress
                }
            }
            invalidate()
        }
        animator.addListener(onEnd = {
            updateMapAfterMove(moveDirection, isPushBox)
            resetAnimationState()
            notifyGameStateChange()
        }, onCancel = {
            resetAnimationState()
        })
        animator.start()
        moveAnimator = animator
    }

    /**
     * 重置动画状态
     */
    private fun resetAnimationState() {
        isAnimating = false
        currentMoveDir = null
        animOffsetX = 0f
        animOffsetY = 0f
        isPushBox = false
        moveAnimator?.cancel()
        moveAnimator = null
    }

    /**
     * 通知游戏状态变化（步数/通关）
     */
    private fun notifyGameStateChange() {
        if (!verifyGameFinished()) { //没过关
            onPushBoxEventListener?.onCurrentGradeMoveNumber(++currentGradleMoveNumber)
            return
        }
        if (currentGradleIndex >= allGradesMapData.size - 1) { //最后一关
            onPushBoxEventListener?.onNotGrade()
            return
        }
        currentGradleIndex++ // 下一关
        onPushBoxEventListener?.onPassGrade(
            currentGradleIndex - 1, currentGradleIndex, currentGradleMoveNumber
        )
    }

    /**
     * 动画结束后更新地图和人物逻辑坐标
     */
    private fun updateMapAfterMove(direction: PushBoxMoveDirection, isPushBox: Boolean) {
        when (direction) {
            PushBoxMoveDirection.RIGHT -> {
                if (isPushBox) {
                    // 推箱子向右移动
                    val newBoxX = manLocationX
                    val newBoxY = manLocationY + 2
                    // 箱子新位置：判断是否是目标点
                    currentMap[newBoxX][newBoxY] = if (originalMap[newBoxX][newBoxY] == GOAL) BOX_AT_GOAL else BOX

                    // 人物新位置（原箱子位置）
                    val newManY = manLocationY + 1
                    currentMap[manLocationX][newManY] = if (originalMap[manLocationX][newManY] == GOAL) MAN_AT_GOAL else MAN

                    // 人物原位置恢复：如果是MAN_AT_GOAL则恢复为GOAL，否则恢复原始地形
                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationY++
                } else {
                    // 普通向右移动
                    val newManY = manLocationY + 1
                    // 新位置：目标点则设为MAN_AT_GOAL，否则设为MAN
                    currentMap[manLocationX][newManY] = if (originalMap[manLocationX][newManY] == GOAL) MAN_AT_GOAL else MAN
                    // 原位置恢复
                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationY++
                }
            }

            PushBoxMoveDirection.LEFT -> {
                if (isPushBox) {
                    // 推箱子向左移动
                    val newBoxX = manLocationX
                    val newBoxY = manLocationY - 2
                    currentMap[newBoxX][newBoxY] = if (originalMap[newBoxX][newBoxY] == GOAL) BOX_AT_GOAL else BOX

                    val newManY = manLocationY - 1
                    currentMap[manLocationX][newManY] = if (originalMap[manLocationX][newManY] == GOAL) MAN_AT_GOAL else MAN

                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationY--
                } else {
                    // 普通向左移动
                    val newManY = manLocationY - 1
                    currentMap[manLocationX][newManY] = if (originalMap[manLocationX][newManY] == GOAL) MAN_AT_GOAL else MAN
                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationY--
                }
            }

            PushBoxMoveDirection.UP -> {
                if (isPushBox) {
                    // 推箱子向上移动
                    val newBoxX = manLocationX - 2
                    val newBoxY = manLocationY
                    currentMap[newBoxX][newBoxY] = if (originalMap[newBoxX][newBoxY] == GOAL) BOX_AT_GOAL else BOX

                    val newManX = manLocationX - 1
                    currentMap[newManX][manLocationY] = if (originalMap[newManX][manLocationY] == GOAL) MAN_AT_GOAL else MAN

                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationX--
                } else {
                    // 普通向上移动
                    val newManX = manLocationX - 1
                    currentMap[newManX][manLocationY] = if (originalMap[newManX][manLocationY] == GOAL) MAN_AT_GOAL else MAN
                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationX--
                }
            }

            PushBoxMoveDirection.DOWN -> {
                if (isPushBox) {
                    // 推箱子向下移动
                    val newBoxX = manLocationX + 2
                    val newBoxY = manLocationY
                    currentMap[newBoxX][newBoxY] = if (originalMap[newBoxX][newBoxY] == GOAL) BOX_AT_GOAL else BOX

                    val newManX = manLocationX + 1
                    currentMap[newManX][manLocationY] = if (originalMap[newManX][manLocationY] == GOAL) MAN_AT_GOAL else MAN

                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationX++
                } else {
                    // 普通向下移动
                    val newManX = manLocationX + 1
                    currentMap[newManX][manLocationY] = if (originalMap[newManX][manLocationY] == GOAL) MAN_AT_GOAL else MAN
                    currentMap[manLocationX][manLocationY] = if (currentMap[manLocationX][manLocationY] == MAN_AT_GOAL) GOAL else originalMap[manLocationX][manLocationY]
                    manLocationX++
                }
            }
        }
    }

    /**
     * 优化绘制逻辑：分层绘制（底→中→顶），解决遮挡问题
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制底层元素（墙、路、目标点）- 最下层
        drawBottomLayer(canvas)
        //绘制中层元素（箱子、箱子在目标点）- 中间层
        drawMiddleLayer(canvas)
        //绘制顶层元素（人物）- 最上层，避免被遮挡
        drawTopLayer(canvas)
    }

    /**
     * 绘制底层元素：墙、路、目标点
     */
    private fun drawBottomLayer(canvas: Canvas) {
        for (i in 0 until currentMapRow) {
            for (j in 0 until currentMapColumn) {
                val tileType = currentMap[i][j]
                if (tileType == 0) continue
                if (tileType in listOf(WALL, ROAD, GOAL)) {
                    val drawX = xoff + j * currentPicSize
                    val drawY = yoff + i * currentPicSize
                    picList[tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
                }
            }
        }
    }

    /**
     * 绘制中层元素：箱子、箱子在目标点（带动画偏移）
     */
    private fun drawMiddleLayer(canvas: Canvas) {
        for (i in 0 until currentMapRow) {
            for (j in 0 until currentMapColumn) {
                val tileType = currentMap[i][j]
                if (tileType == 0) continue
                if (tileType in listOf(BOX, BOX_AT_GOAL)) {
                    var drawX = xoff + j * currentPicSize
                    var drawY = yoff + i * currentPicSize
                    // 被推动的箱子应用动画偏移
                    if (isPushBox && isTargetPushBox(i, j)) {
                        drawX += animOffsetX
                        drawY += animOffsetY
                    }
                    picList[tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
                }
            }
        }
    }

    /**
     * 绘制顶层元素：人物（带动画偏移）
     */
    private fun drawTopLayer(canvas: Canvas) {
        for (i in 0 until currentMapRow) {
            for (j in 0 until currentMapColumn) {
                val tileType = currentMap[i][j]
                if (tileType == 0) continue
                if (tileType == MAN || tileType == MAN_AT_GOAL) {
                    val drawX = xoff + j * currentPicSize + animOffsetX
                    val drawY = yoff + i * currentPicSize + animOffsetY
                    picList[tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
                }
            }
        }
    }

    /**
     * 精准判断当前箱子是否是被推动的目标箱子（基于动画起始坐标）
     */
    private fun isTargetPushBox(i: Int, j: Int): Boolean {
        return when (currentMoveDir) {
            PushBoxMoveDirection.RIGHT -> (i == animStartManX && j == animStartManY + 1)
            PushBoxMoveDirection.LEFT -> (i == animStartManX && j == animStartManY - 1)
            PushBoxMoveDirection.UP -> (i == animStartManX - 1 && j == animStartManY)
            PushBoxMoveDirection.DOWN -> (i == animStartManX + 1 && j == animStartManY)
            else -> false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        moveAnimator?.cancel()
        moveAnimator = null
    }

    private var onPushBoxEventListener: OnPushBoxEventListener? = null

    interface OnPushBoxEventListener {
        fun onCurrentGradeMoveNumber(currentNumber: Int)

        /**
         * 通过当前关卡
         * @param passMapIndex 通过关卡的下标
         * @param nextMapIndex  下一关的下标
         * @param passMoveNum 通过关卡使用的步数
         */
        fun onPassGrade(passMapIndex: Int, nextMapIndex: Int, passMoveNum: Int)//下一关
        fun onNotGrade()
    }

    fun setOnPushBoxEventListener(onPushBoxEventListener: OnPushBoxEventListener) {
        this.onPushBoxEventListener = onPushBoxEventListener
    }
}