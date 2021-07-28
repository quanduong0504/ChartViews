package com.biplus.library.chartlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ChartView : View {
    //纬线数量
    private val LATITUDE_NUM = 5

    //经线数量
    private val LONGITUDE_NUM = 16
    private val mBorderPaint: Paint by lazy { Paint() }
    private val mLineHorizontalPaint: Paint by lazy {
        Paint().apply {
            color = Color.GRAY
            strokeWidth = 2f
        }
    }

    private val mLineVerticalPaint: Paint by lazy {
        Paint().apply {
            color = Color.GRAY
            strokeWidth = 20f
        }
    }

    private val screenWidth: Int by lazy {
        context.resources.displayMetrics.widthPixels
    }

    protected var mQuadrant: Quadrant = object : Quadrant(
            dp2px(5f).toFloat(), dp2px(5f).toFloat(),
            dp2px(5f).toFloat(), dp2px(5f).toFloat()) {
        override fun getQuadrantStartX(): Float {
            return dp2px(10f).toFloat()
        }

        override fun getQuadrantStartY(): Float {
            return dp2px(10f).toFloat()
        }

        override fun getQuadrantWidth(): Float {
            return (measuredWidth - dp2px(10f) * 2).toFloat()
        }

        override fun getQuadrantHeight(): Float {
            return (measuredHeight - dp2px(10f) * 2).toFloat()
        }

    }

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        setupPaint()
    }

    /**
     * 创建画笔
     */
    protected fun setupPaint() {
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = Color.BLACK
        mLineHorizontalPaint.isAntiAlias = true
        mLineHorizontalPaint.color = -0x333334
        mLineVerticalPaint.isAntiAlias = true
        mLineVerticalPaint.color = -0x333334
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureSize(dp2px(200f), widthMeasureSpec),
                measureSize(dp2px(300f), heightMeasureSpec))
    }

    private fun measureSize(size: Int, measureSpec: Int): Int {
        var result = size
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.EXACTLY -> result = specSize
            MeasureSpec.AT_MOST -> result = Math.min(size, specSize)
            MeasureSpec.UNSPECIFIED -> result = size
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawLatitudeBorder(canvas)
//        drawLongitudeBorder(canvas)
        drawHorizontalLine(canvas)
        drawVerticalLine(canvas)
    }

    private fun drawVerticalLines(canvas: Canvas) {
        val w = screenWidth / 50
        for (i in 0 until 10) {
//            canvas.drawLine()
        }
    }

    /**
     * 绘制纬线边框（上、下）
     */
    private fun drawLatitudeBorder(canvas: Canvas) {
        canvas.drawLine(mQuadrant.quadrantStartX, mQuadrant.quadrantStartY,
                mQuadrant.quadrantEndX, mQuadrant.quadrantStartY, mBorderPaint)
        canvas.drawLine(mQuadrant.quadrantStartX, mQuadrant.quadrantEndY,
                mQuadrant.quadrantEndX, mQuadrant.quadrantEndY, mBorderPaint)
    }

    /**
     * 绘制经线边框（左、右）
     */
    private fun drawLongitudeBorder(canvas: Canvas) {
        canvas.drawLine(mQuadrant.quadrantStartX, mQuadrant.quadrantStartY,
                mQuadrant.quadrantStartX, mQuadrant.quadrantEndY, mBorderPaint)
        canvas.drawLine(mQuadrant.quadrantEndX, mQuadrant.quadrantStartY,
                mQuadrant.quadrantEndX, mQuadrant.quadrantEndY, mBorderPaint)
    }

    /**
     * 绘制纬线
     */
    private fun drawHorizontalLine(canvas: Canvas) {
        val offset: Float = (mQuadrant.quadrantPaddingEndY - mQuadrant.quadrantPaddingStartY) / (LATITUDE_NUM
                - 1)
        for (i in 0 until LATITUDE_NUM) {
            canvas.drawLine(mQuadrant.quadrantStartX,
                    offset * i + mQuadrant.quadrantPaddingStartY, mQuadrant.quadrantEndX,
                    offset * i + mQuadrant.quadrantPaddingStartY, mLineHorizontalPaint)
        }
    }

    /**
     * 绘制经线
     */
    private fun drawVerticalLine(canvas: Canvas) {
        val offset: Float = (mQuadrant.quadrantPaddingEndX - mQuadrant.quadrantPaddingStartX) / (LONGITUDE_NUM - 1)
        for (i in 0 until LONGITUDE_NUM) {
//            canvas.drawLine(offset * i + mQuadrant.quadrantPaddingStartX,
//                    mQuadrant.quadrantStartY, offset * i + mQuadrant.quadrantPaddingStartX,
//                    mQuadrant.quadrantEndY, mLineVerticalPaint)
            val rectF = RectF(offset * i + mQuadrant.quadrantPaddingStartX,
                    mQuadrant.quadrantStartY, offset * i + mQuadrant.quadrantPaddingStartX,
                    mQuadrant.quadrantEndY)

            canvas.drawRoundRect(rectF, 10f, 10f, mLineVerticalPaint)
        }
    }

    protected fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}