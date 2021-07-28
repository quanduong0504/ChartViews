package com.biplus.library.chartlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

const val HORIZONTAL_LINE = 5
const val TEXT_HEIGHT = 20f
const val MEASURED_HEIGHT_TEXT = 30f
const val MARGIN_START_END = 34f
const val LINE_MARGIN_TOP = 12f
const val TEXT_SIZE = 12f
const val TOTAL_MARGIN_TOP = 27f
abstract class BaseChartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    protected var mMaxPlace = 200

    protected val mScreenWidth by lazy {
        context.resources.displayMetrics.widthPixels
    }

    protected val mVerticalOffset by lazy {
        (mQuadrant.quadrantPaddingWidth - dp2px(MARGIN_START_END * 2)) /
                (listLabelBottom.size - 1)
    }

    protected val mHeightOffset by lazy {
        mQuadrant.quadrantPaddingHeight / mMaxPlace
    }

    protected val mHorizontalOffset by lazy {
        mQuadrant.quadrantPaddingHeight / (HORIZONTAL_LINE - 1)
    }

    protected val startYItem by lazy {
        mQuadrant.quadrantPaddingStartY + dp2px(LINE_MARGIN_TOP)
    }

    protected val mMaxHeight by lazy {
        mQuadrant.quadrantPaddingEndX - startYItem
    }

    protected val startXItem by lazy {
        mQuadrant.quadrantPaddingStartX + dp2px(MARGIN_START_END)
    }

    protected val mMaxWidth by lazy {
        mQuadrant.quadrantPaddingEndX - (startXItem + dp2px(MARGIN_START_END))
    }

    private val mLineHorizontalPaint: Paint by lazy {
        Paint().apply {
            color = Color.GRAY
            strokeWidth = 2f
        }
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
            return ((measuredHeight - dp2px(MEASURED_HEIGHT_TEXT)) - dp2px(10f) * 2).toFloat()
        }
    }

    abstract fun onDrawVertical(canvas: Canvas)
    abstract val listLabelBottom: List<Pair<String, Int>>

    init {
        mLineHorizontalPaint.isAntiAlias = true
        mLineHorizontalPaint.color = -0x333334
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureSize(dp2px(200f), widthMeasureSpec),
                measureSize(this.height, heightMeasureSpec) + dp2px(MEASURED_HEIGHT_TEXT))
    }

    fun setPlaceValue(max: Int) {
        this.mMaxPlace = max
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawHorizontalLine(canvas)
        drawHorizontalLabel(canvas)
        onDrawVertical(canvas)
    }

    private fun drawHorizontalLabel(canvas: Canvas) {
        listLabelBottom.mapIndexed { index, pair ->
            val first = pair.first
            val second = pair.second

            canvas.drawText(first, startXItem + mVerticalOffset * index, measuredHeight - TEXT_HEIGHT, Paint().apply {
                textSize = dp2px(TEXT_SIZE).toFloat()
                color = Color.GRAY
                textAlign = Paint.Align.CENTER
            })

            canvas.drawCircle(startXItem + mVerticalOffset * index, mQuadrant.quadrantPaddingEndY + dp2px(LINE_MARGIN_TOP), 3f, Paint().apply {
                color = Color.GRAY
            })
        }
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

    private fun drawHorizontalLine(canvas: Canvas) {
        for (i in 0 until HORIZONTAL_LINE) {
            /** Draw line horizontal */
            val startY = mHorizontalOffset * i + mQuadrant.quadrantPaddingStartY + dp2px(LINE_MARGIN_TOP)
            canvas.drawLine(mQuadrant.quadrantStartX,
                    startY, mQuadrant.quadrantEndX,
                    startY, mLineHorizontalPaint)

            /** Draw text bottom */
            canvas.drawText(
                    ((mMaxPlace / (HORIZONTAL_LINE - 1)) * ((HORIZONTAL_LINE - 1) - i)).toString(),
                    mQuadrant.quadrantPaddingEndX,
                    mHorizontalOffset * i + mQuadrant.quadrantPaddingStartY,
                    Paint().apply {
                        textSize = dp2px(TEXT_SIZE).toFloat()
                        color = Color.GRAY
                        textAlign = Paint.Align.RIGHT
                    })
        }
    }

    protected fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}