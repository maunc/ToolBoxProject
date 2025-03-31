package com.us.mauncview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs) {
    /**
     * 存储行的集合，管理行
     */
    private val mLines: MutableList<Line> = ArrayList()

    /**
     * 水平和竖直的间距
     */
    private var flowLayoutAdapter: FlowLayoutAdapter<*>? = null
    private var verticalSpace: Float
    private var horizontalSpace: Float

    // 当前行的指针
    private var mCurrentLine: Line? = null

    // 行的最大宽度，除去边距的宽度
    private var mMaxWidth = 0

    fun setAdapter(flowLayoutAdapter: FlowLayoutAdapter<*>?) {
        this.flowLayoutAdapter = flowLayoutAdapter
        this.flowLayoutAdapter?.setOnDataSetChangedListener {
            setAdapter(this@FlowLayout.flowLayoutAdapter)
        }
        removeAllViews()
        // 循环添加TextView到容器
        val size: Int = flowLayoutAdapter!!.count
        for (i in 0 until size) {
            addView(flowLayoutAdapter.getView(this, i))
        }
    }

    constructor(
        context: Context,
        horizontalSpace: Float,
        verticalSpace: Float,
    ) : this(context, null) {
        this.horizontalSpace = horizontalSpace
        this.verticalSpace = verticalSpace
    }

    init {
        // 获取自定义属性
        val array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
        horizontalSpace = array.getDimension(R.styleable.FlowLayout_widthSpace, 0f)
        verticalSpace = array.getDimension(R.styleable.FlowLayout_heightSpace, 0f)
        array.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 每次测量之前都先清空集合，不让会覆盖掉以前
        mLines.clear()
        mCurrentLine = null
        // 获取总宽度
        val width = MeasureSpec.getSize(widthMeasureSpec)
        // 计算最大的宽度
        mMaxWidth = width - paddingLeft - paddingRight
        // ******************** 测量孩子 ********************
        // 遍历获取孩子
        val childCount = this.childCount
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            // 测量孩子
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            // 测量完需要将孩子添加到管理行的孩子的集合中，将行添加到管理行的集合中
            if (mCurrentLine == null) {
                // 初次添加第一个孩子的时候
                mCurrentLine = Line(mMaxWidth, horizontalSpace)
                if (i == childCount - 1) {
                    mCurrentLine!!.isLast = true
                }
                // 添加孩子
                mCurrentLine!!.addView(childView)
                // 添加行
                mLines.add(mCurrentLine!!)
            } else {
                // 行中有孩子的时候，判断时候能添加
                if (mCurrentLine!!.canAddView(childView)) {
                    // 继续往该行里添加
                    mCurrentLine!!.addView(childView)
                    if (i == childCount - 1) {
                        mCurrentLine!!.isLast = true
                    }
                } else {
                    //  添加到下一行
                    mCurrentLine = Line(mMaxWidth, horizontalSpace)
                    mCurrentLine!!.addView(childView)
                    if (i == childCount - 1) {
                        mCurrentLine!!.isLast = true
                    }
                    mLines.add(mCurrentLine!!)
                }
            }
        }
        // 测量自己只需要计算高度，宽度肯定会被填充满的
        var height = paddingTop + paddingBottom
        for (i in mLines.indices) {
            // 所有行的高度
            height += mLines[i].height
        }
        // 所有竖直的间距
        height = (height + (mLines.size - 1) * verticalSpace).toInt()
        // 测量
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // 这里只负责高度的位置，具体的宽度和子孩子的位置让具体的行去管理
        val l: Int = paddingLeft
        var t: Int = paddingTop
        for (i in mLines.indices) {
            // 获取行
            val line = mLines[i]
            // 管理
            line.layout(t, l)
            // 更新高度
            t += line.height
            if (i != mLines.size - 1) {
                // 不是最后一条就添加间距
                t = (t + verticalSpace).toInt()
            }
        }
    }

    /**
     * 内部类，行管理器，管理每一行的孩子
     */
    inner class Line(
        // 行的最大宽度
        private val maxWidth: Int, // 孩子之间的距离
        private var space: Float,
    ) {
        // 定义一个行的集合来存放子View
        private val views: MutableList<View> = ArrayList()

        // 行中已经使用的宽度
        private var usedWidth = 0

        // 行的高度
        var height: Int = 0
        var isLast: Boolean = false

        /**
         * 往集合里添加孩子
         */
        fun addView(view: View) {
            val childWidth = view.measuredWidth
            val childHeight = view.measuredHeight
            // 更新行的使用宽度和高度
            if (views.size == 0) {
                // 集合里没有孩子的时候
                if (childWidth > maxWidth) {
                    usedWidth = maxWidth
                    height = childHeight
                } else {
                    usedWidth = childWidth
                    height = childHeight
                }
            } else {
                usedWidth = (usedWidth + (childWidth + space)).toInt()
                height = if (childHeight > height) childHeight else height
            }
            // 添加孩子到集合
            views.add(view)
        }

        /**
         * 判断当前的行是否能添加孩子
         */
        fun canAddView(view: View): Boolean {
            // 集合里没有数据可以添加
            if (views.size == 0) {
                return true
            }
            // 最后一个孩子的宽度大于剩余宽度就不添加
            if (view.measuredWidth > (maxWidth - usedWidth - space)) {
                return false
            }
            // 默认可以添加
            return true
        }

        /**
         * 指定孩子显示的位置
         */
        fun layout(topNum: Int, leftNum: Int) {
            // 平分剩下的空间
            var l = leftNum
            val avg = (maxWidth - usedWidth) / views.size
            // 循环指定孩子位置
            for (view in views) {
                // 获取宽高
                var measuredWidth = view.measuredWidth
                val measuredHeight = view.measuredHeight
                if (isLast) {
                    // 重新测量
                    view.measure(
                        MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
                    )
                } else {
                    // 重新测量
                    view.measure(
                        MeasureSpec.makeMeasureSpec(measuredWidth + avg, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
                    )
                }
                // 重新获取宽度值
                measuredWidth = view.measuredWidth
                val left = l
                val right = measuredWidth + left
                val bottom = measuredHeight + topNum
                // 指定位置
                view.layout(left, topNum, right, bottom)
                // 更新数据
                l = (l + (measuredWidth + space)).toInt()
            }
        }
    }
}

abstract class FlowLayoutAdapter<T>(
    private val dataList: MutableList<T> = mutableListOf(),
) {
    private var onDataSetChangedListener: OnDataSetChangedListener? = null

    val count: Int get() = dataList.size

    fun getView(parent: FlowLayout, position: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(
            bindItemLayoutId(position, dataList[position]), parent, false
        )
        bindDataToView(ViewHolder(view), position, dataList[position])
        return view
    }

    //填充数据
    abstract fun bindDataToView(holder: ViewHolder?, position: Int, bean: T)

    abstract fun bindItemLayoutId(position: Int, bean: T): Int

    fun remove(position: Int) {
        dataList.removeAt(position)
        notifyDataSetChanged()
    }

    fun add(bean: T) {
        dataList.add(bean)
        notifyDataSetChanged()
    }

    fun notifyDataSetChanged() {
        onDataSetChangedListener?.onDataSetChanged()
    }

    fun addAll(beans: List<T>) {
        dataList.addAll(beans)
        notifyDataSetChanged()
    }

    fun clearAddAll(beans: List<T>) {
        dataList.clear()
        dataList.addAll(beans)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun setOnDataSetChangedListener(onDataSetChangedListener: OnDataSetChangedListener?) {
        this.onDataSetChangedListener = onDataSetChangedListener
    }

    fun interface OnDataSetChangedListener {
        fun onDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder(private val itemView: View) {
        private val arrayView = SparseArray<View?>()
        fun <T : View?> getView(viewId: Int): T? {
            var view = arrayView[viewId]
            if (view == null) {
                view = itemView.findViewById(viewId)
                arrayView.put(viewId, view)
            }
            return view as T?
        }
    }
}