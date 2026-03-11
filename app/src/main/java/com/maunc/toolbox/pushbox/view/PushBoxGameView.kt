package com.maunc.toolbox.pushbox.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import com.maunc.toolbox.R
import com.maunc.toolbox.commonbase.ext.obtainDrawable
import com.maunc.toolbox.commonbase.ext.obtainScreenHeight
import com.maunc.toolbox.commonbase.ext.obtainScreenWidth
import com.maunc.toolbox.pushbox.constant.BOX
import com.maunc.toolbox.pushbox.constant.BOX_AT_GOAL
import com.maunc.toolbox.pushbox.constant.GOAL
import com.maunc.toolbox.pushbox.constant.MAN
import com.maunc.toolbox.pushbox.constant.ROAD
import com.maunc.toolbox.pushbox.constant.WALL
import com.maunc.toolbox.pushbox.constant.allGradesMapData
import com.maunc.toolbox.pushbox.constant.obtainTargetMap
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@SuppressLint("NotConstructor")
class PushBoxGameView(
    context: Context?,
    attrs: AttributeSet?,
) : View(context, attrs) {
    private var screenWidth = obtainScreenWidth() // 屏幕宽
    private var screenHeight = obtainScreenHeight() // 屏幕高
    private var currentGradleIndex = 0 // 当前关数
    private var currentMap: Array<IntArray>? = null // 当前地图(随着人物变化而变化)
    private var originalMap: Array<IntArray>? = null // 当前地图的原始地图
    private var currentMapRow = 0 // 当前地图行数
    private var currentMapColumn = 0 // 当前地图列数
    private var manLocationX = 0 // 人所在行
    private var manLocationY = 0 // 人所在列
    private var currentGradleMoveNumber = 0

    private var xoff = 10f // 左边距
    private var yoff = 20f // 上边距
    private var picList: Array<Bitmap?>? = null // 图片
    private var currentPicSize = 0 // 图片大小
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }
    private var isAnimating = false // 是否正在播放移动动画
    private var isPushBox = false // 是否是推箱子移动
    private var currentMoveDir: MoveDirection? = null // 当前移动方向
    private var moveAnimator: ValueAnimator? = null // 移动动画对象
    private var animStartManX = 0 // 动画起始时人物X坐标
    private var animStartManY = 0 // 动画起始时人物Y坐标
    private var animOffsetX = 0f // X轴动画偏移量
    private var animOffsetY = 0f // Y轴动画偏移量

    //测量大小
    private var measuredViewWidth = 0
    private var measuredViewHeight = 0

    enum class MoveDirection {
        UP, DOWN, LEFT, RIGHT
    }

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
            error.invoke("关卡不合理")
            return
        }
        this.currentGradleIndex = index
        initMap()
        invalidate()
    }

    //获取当前关卡
    fun obtainCurrentGradleIndex() = Pair(currentGradleIndex, currentMap)

    /**
     * 初始化地图
     */
    private fun initMap() {
        currentMap = obtainTargetMap(currentGradleIndex)
        originalMap = obtainTargetMap(currentGradleIndex)

        /**初始化当前地图的信息*/
        currentMapRow = currentMap!!.size
        currentMapColumn = if (currentMapRow > 0) currentMap!![0].size else 0
        xoff = 30f
        yoff = 60f
        val maxSide = max(currentMapRow, currentMapColumn)
        // 增加边距，避免元素贴边/挤压
        val availableWidth = screenWidth - 2 * xoff - 20
        val availableHeight = screenHeight - yoff - 40
        val s1 = floor((availableWidth / maxSide).toDouble()).toInt()
        val s2 = floor((availableHeight / maxSide).toDouble()).toInt()
        currentPicSize = min(s1, s2)
        currentPicSize = max(currentPicSize, 40)
        //获取人物位置
        for (i in currentMap!!.indices) {
            for (j in currentMap!![0].indices) {
                if (currentMap!![i][j] == MAN) {
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
        picList = arrayOfNulls(7)
        obtainDrawable(R.drawable.icon_push_box_qiang)?.let { loadPic(WALL, it) }
        obtainDrawable(R.drawable.icon_push_box_goal)?.let { loadPic(GOAL, it) }
        obtainDrawable(R.drawable.icon_push_box_lu)?.let { loadPic(ROAD, it) }
        obtainDrawable(R.drawable.icon_push_box_xiangzi)?.let { loadPic(BOX, it) }
        obtainDrawable(R.drawable.icon_push_box_boxgoal)?.let { loadPic(BOX_AT_GOAL, it) }
        obtainDrawable(R.drawable.icon_push_box_ren)?.let { loadPic(MAN, it) }
    }

    //加载图片
    private fun loadPic(key: Int, tile: Drawable) {
        val bitmap = Bitmap.createBitmap(currentPicSize, currentPicSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        tile.setBounds(0, 0, currentPicSize, currentPicSize)
        tile.draw(canvas)
        picList!![key] = bitmap
    }

    /**
     * 检验游戏是否结束
     */
    fun verifyGameFinished(): Boolean {
        var finish = true
        for (i in 0 until currentMapRow) {
            for (j in 0 until currentMapColumn) {
                if (currentMap!![i][j] == GOAL || currentMap!![i][j] == BOX) {
                    finish = false
                }
            }
        }
        return finish
    }

    // 人物向右移动
    fun moveRight() {
        if (isAnimating || moveAnimator?.isRunning == true) return
        // 判断是否可移动 + 是否需要推箱子
        val (canMove, needPushBox) = checkMoveRightCondition()
        if (!canMove) return
        startMoveAnimation(MoveDirection.RIGHT, needPushBox)
    }

    // 人物向左移动
    fun moveLeft() {
        if (isAnimating || moveAnimator?.isRunning == true) return
        val (canMove, needPushBox) = checkMoveLeftCondition()
        if (!canMove) return
        startMoveAnimation(MoveDirection.LEFT, needPushBox)
    }

    // 人物向上移动
    fun moveUp() {
        if (isAnimating || moveAnimator?.isRunning == true) return
        val (canMove, needPushBox) = checkMoveUpCondition()
        if (!canMove) return
        startMoveAnimation(MoveDirection.UP, needPushBox)
    }

    // 人物向下移动
    fun moveDown() {
        if (isAnimating || moveAnimator?.isRunning == true) return
        val (canMove, needPushBox) = checkMoveDownCondition()
        if (!canMove) return
        startMoveAnimation(MoveDirection.DOWN, needPushBox)
    }

    // 检查向右移动的条件
    private fun checkMoveRightCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manLocationX][manLocationY + 1] == BOX || currentMap!![manLocationX][manLocationY + 1] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manLocationX][manLocationY + 2] == GOAL || currentMap!![manLocationX][manLocationY + 2] == ROAD
        } else {
            currentMap!![manLocationX][manLocationY + 1] == ROAD || currentMap!![manLocationX][manLocationY + 1] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    // 检查向左移动的条件
    private fun checkMoveLeftCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manLocationX][manLocationY - 1] == BOX || currentMap!![manLocationX][manLocationY - 1] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manLocationX][manLocationY - 2] == GOAL || currentMap!![manLocationX][manLocationY - 2] == ROAD
        } else {
            currentMap!![manLocationX][manLocationY - 1] == ROAD || currentMap!![manLocationX][manLocationY - 1] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    // 检查向上移动的条件
    private fun checkMoveUpCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manLocationX - 1][manLocationY] == BOX || currentMap!![manLocationX - 1][manLocationY] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manLocationX - 2][manLocationY] == GOAL || currentMap!![manLocationX - 2][manLocationY] == ROAD
        } else {
            currentMap!![manLocationX - 1][manLocationY] == ROAD || currentMap!![manLocationX - 1][manLocationY] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    // 检查向下移动的条件
    private fun checkMoveDownCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manLocationX + 1][manLocationY] == BOX || currentMap!![manLocationX + 1][manLocationY] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manLocationX + 2][manLocationY] == GOAL || currentMap!![manLocationX + 2][manLocationY] == ROAD
        } else {
            currentMap!![manLocationX + 1][manLocationY] == ROAD || currentMap!![manLocationX + 1][manLocationY] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    /**
     * 启动移动动画
     * @param moveDirection 移动方向
     * @param isPushBox 是否推箱子
     */
    private fun startMoveAnimation(moveDirection: MoveDirection, isPushBox: Boolean) {
        this.isAnimating = true
        this.currentMoveDir = moveDirection
        this.isPushBox = isPushBox
        animOffsetX = 0f
        animOffsetY = 0f
        // 记录动画起始时的人物坐标
        animStartManX = manLocationX
        animStartManY = manLocationY

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 50
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            when (moveDirection) {
                MoveDirection.RIGHT -> {
                    animOffsetX = currentPicSize * progress
                    animOffsetY = 0f
                }

                MoveDirection.LEFT -> {
                    animOffsetX = -currentPicSize * progress
                    animOffsetY = 0f
                }

                MoveDirection.UP -> {
                    animOffsetX = 0f
                    animOffsetY = -currentPicSize * progress
                }

                MoveDirection.DOWN -> {
                    animOffsetX = 0f
                    animOffsetY = currentPicSize * progress
                }
            }
            invalidate()
        }
        animator.addListener(onEnd = {
            updateMapAfterMove(moveDirection, isPushBox)
            isAnimating = false
            animOffsetX = 0f
            animOffsetY = 0f
            currentMoveDir = null
            this.isPushBox = false
            if (verifyGameFinished()) {
                //重置信息
                currentGradleMoveNumber = 0
                onPushBoxEventListener?.onCurrentGradeMoveNumber(currentGradleMoveNumber)
                if (currentGradleIndex < allGradesMapData.size - 1) {
                    currentGradleIndex++
                    onPushBoxEventListener?.onNextGrade(
                        currentGradleIndex, allGradesMapData[currentGradleIndex]
                    )
                } else {
                    onPushBoxEventListener?.onNotGrade()
                }
            } else {
                onPushBoxEventListener?.onCurrentGradeMoveNumber(++this.currentGradleMoveNumber)
            }
        }, onCancel = {
            isAnimating = false
            animOffsetX = 0f
            animOffsetY = 0f
            currentMoveDir = null
            this.isPushBox = false
        })
        animator.start()
        moveAnimator = animator
    }

    /**
     * 动画结束后更新地图和人物逻辑坐标
     */
    private fun updateMapAfterMove(moveDirection: MoveDirection, isPushBox: Boolean) {
        when (moveDirection) {
            MoveDirection.RIGHT -> {
                if (isPushBox) {
                    // 更新箱子位置
                    currentMap!![manLocationX][manLocationY + 2] =
                        if (currentMap!![manLocationX][manLocationY + 2] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manLocationX][manLocationY + 1] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationY++
                } else {
                    // 更新人物位置
                    currentMap!![manLocationX][manLocationY + 1] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationY++
                }
            }

            MoveDirection.LEFT -> {
                if (isPushBox) {
                    currentMap!![manLocationX][manLocationY - 2] =
                        if (currentMap!![manLocationX][manLocationY - 2] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manLocationX][manLocationY - 1] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationY--
                } else {
                    currentMap!![manLocationX][manLocationY - 1] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationY--
                }
            }

            MoveDirection.UP -> {
                if (isPushBox) {
                    currentMap!![manLocationX - 2][manLocationY] =
                        if (currentMap!![manLocationX - 2][manLocationY] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manLocationX - 1][manLocationY] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationX--
                } else {
                    currentMap!![manLocationX - 1][manLocationY] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationX--
                }
            }

            MoveDirection.DOWN -> {
                if (isPushBox) {
                    currentMap!![manLocationX + 2][manLocationY] =
                        if (currentMap!![manLocationX + 2][manLocationY] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manLocationX + 1][manLocationY] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationX++
                } else {
                    currentMap!![manLocationX + 1][manLocationY] = MAN
                    currentMap!![manLocationX][manLocationY] =
                        roadOrGoal(manLocationX, manLocationY)
                    manLocationX++
                }
            }
        }
        invalidate()
    }

    /*
     * 人所在的位置原来是路还是目标区域
     * 使用原始地图tem来判断
     * 看新地图人所在的位置在原始地图是什么角色
     */
    private fun roadOrGoal(x: Int, y: Int): Int {
        var result = ROAD
        if (originalMap!![x][y] == GOAL) {
            result = GOAL
        }
        return result
    }

    /**
     * 优化绘制逻辑：分层绘制（底→中→顶），解决遮挡问题
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (currentMap == null || picList == null) return
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
                val tileType = currentMap!![i][j]
                if (tileType == 0) continue
                if (tileType in listOf(WALL, ROAD, GOAL)) {
                    val drawX = xoff + j * currentPicSize
                    val drawY = yoff + i * currentPicSize
                    picList!![tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
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
                val tileType = currentMap!![i][j]
                if (tileType == 0) continue
                if (tileType in listOf(BOX, BOX_AT_GOAL)) {
                    var drawX = xoff + j * currentPicSize
                    var drawY = yoff + i * currentPicSize
                    // 被推动的箱子应用动画偏移
                    if (isPushBox && isTargetPushBox(i, j)) {
                        drawX += animOffsetX
                        drawY += animOffsetY
                    }
                    picList!![tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
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
                val tileType = currentMap!![i][j]
                if (tileType == 0) continue
                if (tileType == MAN) {
                    val drawX = xoff + j * currentPicSize + animOffsetX
                    val drawY = yoff + i * currentPicSize + animOffsetY
                    picList!![tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
                }
            }
        }
    }

    /**
     * 精准判断当前箱子是否是被推动的目标箱子（基于动画起始坐标）
     */
    private fun isTargetPushBox(i: Int, j: Int): Boolean {
        return when (currentMoveDir) {
            MoveDirection.RIGHT -> (i == animStartManX && j == animStartManY + 1)
            MoveDirection.LEFT -> (i == animStartManX && j == animStartManY - 1)
            MoveDirection.UP -> (i == animStartManX - 1 && j == animStartManY)
            MoveDirection.DOWN -> (i == animStartManX + 1 && j == animStartManY)
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
        fun onNextGrade(mapIndex: Int, map: ArrayList<ArrayList<Int>>)//下一关
        fun onNotGrade()//没有关卡了
    }

    fun setOnPushBoxEventListener(onPushBoxEventListener: OnPushBoxEventListener) {
        this.onPushBoxEventListener = onPushBoxEventListener
    }
}