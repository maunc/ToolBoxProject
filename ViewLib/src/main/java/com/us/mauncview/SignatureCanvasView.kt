package com.us.mauncview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class SignatureCanvasView : View {

    private var widthSize = 0
    private var heightSize = 0
    private var bitmap: Bitmap? = null //整个画板显示的位图
    private lateinit var penPaint: Paint //画板的画笔
    private lateinit var eraserPaint: Paint
    private val canvas = Canvas() //画板的画布
    private var xTouch = 0f
    private var yTouch = 0f
    private var isPen = true
    private val rgbMaxValue = 255

    //橡皮的宽度
    private var eraserWidth = 50

    //笔的宽度
    private var penWidth = 7

    constructor(
        context: Context,
    ) : super(context) {
        initConfig()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
    ) : super(context, attrs) {
        initConfig()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
    ) : super(context, attrs, defStyleAttr) {
        initConfig()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(
            "SignatureCanvasView",
            "onMeasure widthMeasureSpec=$widthMeasureSpec,heightMeasureSpec=$heightMeasureSpec"
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        Log.d(
            "SignatureCanvasView",
            "onMeasure widthSize=$widthSize,heightSize=$heightSize,widthMode=$widthMode,heightMode=$heightMode"
        )
        initBitmap()
    }

    private fun initConfig() {
        //设置为可点击才能获取到MotionEvent.ACTION_MOVE
        isClickable = true
        penPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = penWidth.toFloat()
            //设置是否使用抗锯齿功能，抗锯齿功能会消耗较大资源，绘制图形的速度会减慢
            isAntiAlias = true
            //设置是否使用图像抖动处理，会使图像颜色更加平滑饱满，更加清晰
            isDither = true
        }

        eraserPaint = Paint().apply {
            //橡皮颜色和底层画板颜色需要一样
            color = Color.rgb(rgbMaxValue, rgbMaxValue, rgbMaxValue)
            style = Paint.Style.STROKE
            strokeWidth = eraserWidth.toFloat()
            isAntiAlias = true
            isDither = true
        }
    }

    private fun initBitmap() {
        if (null != bitmap) {
            bitmap!!.recycle()
        }
        bitmap = Bitmap.createBitmap(widthSize, heightSize, Bitmap.Config.ARGB_8888)
        bitmap?.let { bmp ->
            canvas.setBitmap(bmp)
            canvas.drawRect(
                0f, 0f, bmp.width.toFloat(), bmp.height.toFloat(), Paint().apply {
                    //底层画板的颜色
                    color = Color.rgb(rgbMaxValue, rgbMaxValue, rgbMaxValue)
                }
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, penPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xTouch = event.x
                yTouch = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                canvas.drawLine(
                    xTouch, yTouch, event.x, event.y, if (isPen) penPaint else eraserPaint
                )
                xTouch = event.x
                yTouch = event.y
                invalidate()
            }

            MotionEvent.ACTION_UP -> {}
        }
        return super.onTouchEvent(event)
    }

//    fun testDrawLine() {
//        val lists: MutableList<EventBaseBean> = ArrayList()
//        for (i in 101..299) {
//            lists.add(EventBaseBean(i, i))
//        }
//        for (i in 0 until lists.size) {
//            val eventBaseBean = lists[i]
//            canvas.drawLine(
//                100f,
//                100f,
//                eventBaseBean.eventX.toFloat(),
//                eventBaseBean.eventY.toFloat(),
//                if (isPen) penPaint else eraserPaint
//            )
//        }
//        invalidate()
//    }
//
//    class EventBaseBean(internal val eventX: Int, val eventY: Int)

    fun setPenWidth(penWidth: Int) {
        this.penWidth = penWidth
    }

    fun setEraserWidth(eraserWidth: Int) {
        this.eraserWidth = eraserWidth
    }

    /**
     * 获取画好的电子签名
     */
    fun getBitmap(): Bitmap {
        return bitmap!!
    }

    /**
     * 清除电子签名
     */
    fun clear() {
        initBitmap()
        invalidate()
    }

    fun setPenMode() {
        isPen = true
    }

    fun setEraserMode() {
        isPen = false
    }
}