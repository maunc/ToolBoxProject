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
import android.widget.Toast
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

/**
 * ClsFunction：推箱子游戏视图（带平滑移动动画）
 * CreateDate：2024/5/16
 * Author：TimeWillRememberUs
 * Optimize：修复动画遮挡、优化偏移计算、分层绘制、增强动画流畅度
 */
@SuppressLint("NotConstructor")
class PushBoxGameView(
    context: Context?,
    attrs: AttributeSet?,
) : View(context, attrs) {
    private var gate = 0 //当前关数
    private var currentMap: Array<IntArray>? = null //当前地图
    private var originalMap: Array<IntArray>? = null //原始地图
    private var width = obtainScreenWidth() //宽
    private var height = obtainScreenHeight() //高
    private var mapRow = 0 //地图行数
    private var mapColumn = 0 //地图列数
    private var manX = 0 //人所在行
    private var manY = 0 //人所在列
    private var xoff = 10f //左边距
    private var yoff = 20f //上边距
    private var pic: Array<Bitmap?>? = null//图片
    private var picSize = 0 //图片大小
    private val paint: Paint = Paint().apply {
        isAntiAlias = true // 抗锯齿
        isFilterBitmap = true // 位图过滤，动画更平滑
        isDither = true // 抖动，减少色彩断层
    }
    private var isAnimating = false // 是否正在播放移动动画
    private var isPushBox = false // 是否是推箱子移动
    private var currentMoveDir: Direction? = null // 当前移动方向
    private var moveAnimator: ValueAnimator? = null // 移动动画对象
    private var animStartManX = 0 // 动画起始时人物X坐标
    private var animStartManY = 0 // 动画起始时人物Y坐标
    private var animOffsetX = 0f // X轴动画偏移量（像素）
    private var animOffsetY = 0f // Y轴动画偏移量（像素）


    private var measuredViewWidth = 0
    private var measuredViewHeight = 0

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    init {
        this.isFocusable = true
        initMap()
        initPic()
    }

    /**
     * 初始化地图
     */
    private fun initMap() {
        currentMap = obtainTargetMap(gate)
        originalMap = obtainTargetMap(gate)
        getMapDetail()
        getManPosition()
    }

    //获取地图详细信息（优化尺寸计算，避免元素挤压）
    private fun getMapDetail() {
        mapRow = currentMap!!.size
        mapColumn = if (mapRow > 0) currentMap!![0].size else 0
        xoff = 30f
        yoff = 60f
        val maxSide = max(mapRow, mapColumn)
        // 增加边距，避免元素贴边/挤压
        val availableWidth = width - 2 * xoff - 20
        val availableHeight = height - yoff - 40
        val s1 = floor((availableWidth / maxSide).toDouble()).toInt()
        val s2 = floor((availableHeight / maxSide).toDouble()).toInt()
        picSize = min(s1, s2)
        // 确保tileSize最小为40，避免元素过小
        picSize = max(picSize, 40)
    }

    //获取人物位置
    private fun getManPosition() {
        for (i in currentMap!!.indices) {
            for (j in currentMap!![0].indices) {
                if (currentMap!![i][j] == MAN) {
                    manX = i
                    manY = j
                    break
                }
            }
        }
    }

    //初始化图片资源
    private fun initPic() {
        pic = arrayOfNulls(7)
        obtainDrawable(R.drawable.icon_push_box_qiang)?.let { loadPic(WALL, it) }
        obtainDrawable(R.drawable.icon_push_box_goal)?.let { loadPic(GOAL, it) }
        obtainDrawable(R.drawable.icon_push_box_lu)?.let { loadPic(ROAD, it) }
        obtainDrawable(R.drawable.icon_push_box_xiangzi)?.let { loadPic(BOX, it) }
        obtainDrawable(R.drawable.icon_push_box_boxgoal)?.let { loadPic(BOX_AT_GOAL, it) }
        obtainDrawable(R.drawable.icon_push_box_ren)?.let { loadPic(MAN, it) }
    }

    //加载图片
    private fun loadPic(key: Int, tile: Drawable) {
        val bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        tile.setBounds(0, 0, picSize, picSize)
        tile.draw(canvas)
        pic!![key] = bitmap
    }

    //更换关卡
    private fun nextGate() {
        if (gate < allGradesMapData.size - 1) {
            gate++
        } else {
            Toast.makeText(this.context, "最后一关了", Toast.LENGTH_SHORT).show()
        }
        reInitMap()
    }

    private fun reInitMap() {
        initMap()
        initPic()
    }

    //如果在地图上找不到空的目标区域或者可移动的箱子，则游戏结束
    private fun gameFinished(): Boolean {
        var finish = true
        for (i in 0 until mapRow) {
            for (j in 0 until mapColumn) {
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
        startMoveAnimation(Direction.RIGHT, needPushBox)
    }

    // 人物向左移动
    fun moveLeft() {
        if (isAnimating || moveAnimator?.isRunning == true) return
        val (canMove, needPushBox) = checkMoveLeftCondition()
        if (!canMove) return
        startMoveAnimation(Direction.LEFT, needPushBox)
    }

    // 人物向上移动
    fun moveUp() {
        if (isAnimating || moveAnimator?.isRunning == true) return
        val (canMove, needPushBox) = checkMoveUpCondition()
        if (!canMove) return
        startMoveAnimation(Direction.UP, needPushBox)
    }

    // 人物向下移动
    fun moveDown() {
        if (isAnimating || moveAnimator?.isRunning == true) return
        val (canMove, needPushBox) = checkMoveDownCondition()
        if (!canMove) return
        startMoveAnimation(Direction.DOWN, needPushBox)
    }

    // 检查向右移动的条件
    private fun checkMoveRightCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manX][manY + 1] == BOX || currentMap!![manX][manY + 1] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manX][manY + 2] == GOAL || currentMap!![manX][manY + 2] == ROAD
        } else {
            currentMap!![manX][manY + 1] == ROAD || currentMap!![manX][manY + 1] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    // 检查向左移动的条件
    private fun checkMoveLeftCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manX][manY - 1] == BOX || currentMap!![manX][manY - 1] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manX][manY - 2] == GOAL || currentMap!![manX][manY - 2] == ROAD
        } else {
            currentMap!![manX][manY - 1] == ROAD || currentMap!![manX][manY - 1] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    // 检查向上移动的条件
    private fun checkMoveUpCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manX - 1][manY] == BOX || currentMap!![manX - 1][manY] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manX - 2][manY] == GOAL || currentMap!![manX - 2][manY] == ROAD
        } else {
            currentMap!![manX - 1][manY] == ROAD || currentMap!![manX - 1][manY] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    // 检查向下移动的条件
    private fun checkMoveDownCondition(): Pair<Boolean, Boolean> {
        val needPushBox =
            currentMap!![manX + 1][manY] == BOX || currentMap!![manX + 1][manY] == BOX_AT_GOAL
        val canMove = if (needPushBox) {
            currentMap!![manX + 2][manY] == GOAL || currentMap!![manX + 2][manY] == ROAD
        } else {
            currentMap!![manX + 1][manY] == ROAD || currentMap!![manX + 1][manY] == GOAL
        }
        return Pair(canMove, needPushBox)
    }

    /**
     * 启动移动动画（优化偏移计算、增加动画起始坐标记录）
     * @param direction 移动方向
     * @param isPushBox 是否推箱子
     */
    private fun startMoveAnimation(direction: Direction, isPushBox: Boolean) {
        this.isAnimating = true
        this.currentMoveDir = direction
        this.isPushBox = isPushBox
        animOffsetX = 0f
        animOffsetY = 0f
        // 记录动画起始时的人物坐标（修复箱子偏移判断）
        animStartManX = manX
        animStartManY = manY

        // 创建值动画：0 → 1（比例值），避免直接用像素值导致越界
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 60
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            when (direction) {
                Direction.RIGHT -> {
                    animOffsetX = picSize * progress
                    animOffsetY = 0f
                }

                Direction.LEFT -> {
                    animOffsetX = -picSize * progress
                    animOffsetY = 0f
                }

                Direction.UP -> {
                    animOffsetX = 0f
                    animOffsetY = -picSize * progress
                }

                Direction.DOWN -> {
                    animOffsetX = 0f
                    animOffsetY = picSize * progress
                }
            }
            invalidate()
        }
        animator.addListener(onEnd = {
            // 更新地图和人物逻辑坐标
            updateMapAfterMove(direction, isPushBox)
            isAnimating = false
            animOffsetX = 0f
            animOffsetY = 0f
            currentMoveDir = null
            this@PushBoxGameView.isPushBox = false

            // 检查是否通关，切换关卡
            if (gameFinished()) {
                nextGate()
            }
        }, onCancel = {
            isAnimating = false
            animOffsetX = 0f
            animOffsetY = 0f
            currentMoveDir = null
            this@PushBoxGameView.isPushBox = false
        })
        // 启动动画
        animator.start()
        moveAnimator = animator
    }

    /**
     * 动画结束后更新地图和人物逻辑坐标
     */
    private fun updateMapAfterMove(direction: Direction, isPushBox: Boolean) {
        when (direction) {
            Direction.RIGHT -> {
                if (isPushBox) {
                    // 推箱子：更新箱子位置
                    currentMap!![manX][manY + 2] =
                        if (currentMap!![manX][manY + 2] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manX][manY + 1] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manY++
                } else {
                    // 普通移动：更新人物位置
                    currentMap!![manX][manY + 1] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manY++
                }
            }

            Direction.LEFT -> {
                if (isPushBox) {
                    currentMap!![manX][manY - 2] =
                        if (currentMap!![manX][manY - 2] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manX][manY - 1] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manY--
                } else {
                    currentMap!![manX][manY - 1] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manY--
                }
            }

            Direction.UP -> {
                if (isPushBox) {
                    currentMap!![manX - 2][manY] =
                        if (currentMap!![manX - 2][manY] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manX - 1][manY] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manX--
                } else {
                    currentMap!![manX - 1][manY] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manX--
                }
            }

            Direction.DOWN -> {
                if (isPushBox) {
                    currentMap!![manX + 2][manY] =
                        if (currentMap!![manX + 2][manY] == GOAL) BOX_AT_GOAL else BOX
                    currentMap!![manX + 1][manY] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manX++
                } else {
                    currentMap!![manX + 1][manY] = MAN
                    currentMap!![manX][manY] = roadOrGoal(manX, manY)
                    manX++
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
        if (currentMap == null || pic == null) return
        // 第一步：绘制底层元素（墙、路、目标点）- 最下层
        drawBottomLayer(canvas)
        // 第二步：绘制中层元素（箱子、箱子在目标点）- 中间层
        drawMiddleLayer(canvas)
        // 第三步：绘制顶层元素（人物）- 最上层，避免被遮挡
        drawTopLayer(canvas)
    }

    /**
     * 绘制底层元素：墙、路、目标点
     */
    private fun drawBottomLayer(canvas: Canvas) {
        for (i in 0 until mapRow) {
            for (j in 0 until mapColumn) {
                val tileType = currentMap!![i][j]
                if (tileType == 0) continue
                if (tileType in listOf(WALL, ROAD, GOAL)) {
                    val drawX = xoff + j * picSize
                    val drawY = yoff + i * picSize
                    pic!![tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
                }
            }
        }
    }

    /**
     * 绘制中层元素：箱子、箱子在目标点（带动画偏移）
     */
    private fun drawMiddleLayer(canvas: Canvas) {
        for (i in 0 until mapRow) {
            for (j in 0 until mapColumn) {
                val tileType = currentMap!![i][j]
                if (tileType == 0) continue
                if (tileType in listOf(BOX, BOX_AT_GOAL)) {
                    var drawX = xoff + j * picSize
                    var drawY = yoff + i * picSize
                    // 被推动的箱子应用动画偏移
                    if (isPushBox && isTargetPushBox(i, j)) {
                        drawX += animOffsetX
                        drawY += animOffsetY
                    }
                    pic!![tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
                }
            }
        }
    }

    /**
     * 绘制顶层元素：人物（带动画偏移）
     */
    private fun drawTopLayer(canvas: Canvas) {
        for (i in 0 until mapRow) {
            for (j in 0 until mapColumn) {
                val tileType = currentMap!![i][j]
                if (tileType == 0) continue
                if (tileType == MAN) {
                    val drawX = xoff + j * picSize + animOffsetX
                    val drawY = yoff + i * picSize + animOffsetY
                    pic!![tileType]?.let { canvas.drawBitmap(it, drawX, drawY, paint) }
                }
            }
        }
    }

    /**
     * 精准判断当前箱子是否是被推动的目标箱子（基于动画起始坐标）
     */
    private fun isTargetPushBox(i: Int, j: Int): Boolean {
        return when (currentMoveDir) {
            Direction.RIGHT -> (i == animStartManX && j == animStartManY + 1)
            Direction.LEFT -> (i == animStartManX && j == animStartManY - 1)
            Direction.UP -> (i == animStartManX - 1 && j == animStartManY)
            Direction.DOWN -> (i == animStartManX + 1 && j == animStartManY)
            else -> false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        // 计算默认尺寸（适配wrap_content）
        val defaultWidth = (obtainScreenWidth() * 0.55f).toInt()
        val defaultHeight = (obtainScreenHeight() * 0.55f).toInt()

        measuredViewWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize // 固定尺寸或match_parent
            MeasureSpec.AT_MOST -> min(defaultWidth, widthSize) // wrap_content，不超过父容器
            else -> defaultWidth // UNSPECIFIED，使用默认尺寸
        }
        measuredViewHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(defaultHeight, heightSize)
            else -> defaultHeight
        }
        setMeasuredDimension(measuredViewWidth, measuredViewHeight)

        getMapDetail()
        initPic()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            measuredViewWidth = w
            measuredViewHeight = h
            getMapDetail()
            initPic()
            invalidate()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        moveAnimator?.cancel()
        moveAnimator = null
    }
}