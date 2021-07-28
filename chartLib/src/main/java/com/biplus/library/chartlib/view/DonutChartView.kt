package com.biplus.library.chartlib.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import androidx.core.view.doOnPreDraw
import com.biplus.library.chartlib.R
import com.biplus.library.chartlib.animation.ChartAnimation
import com.biplus.library.chartlib.animation.DefaultDonutAnimation
import com.biplus.library.chartlib.animation.DonutNoAnimation
import com.biplus.library.chartlib.data.DonutDataPoint
import com.biplus.library.chartlib.data.Frame
import com.biplus.library.chartlib.data.Paddings
import com.biplus.library.chartlib.data.configuration.DonutChartConfiguration
import com.biplus.library.chartlib.data.toRect
import com.biplus.library.chartlib.extensions.obtainStyledAttributes
import com.biplus.library.chartlib.helpers.ChartContract
import com.biplus.library.chartlib.renderer.DonutChartRenderer

class DonutChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ChartContract.DonutView {

    @Suppress("MemberVisibilityCanBePrivate")
    var donutThickness = defaultThickness

    @Suppress("MemberVisibilityCanBePrivate")
    var donutColors = intArrayOf(defaultColor)

    @Suppress("MemberVisibilityCanBePrivate")
    var donutBackgroundColor = defaultBackgroundColor

    @Suppress("MemberVisibilityCanBePrivate")
    var donutRoundCorners = false

    @Suppress("MemberVisibilityCanBePrivate")
    var donutTotal = defaultDonutTotal

    var animation: ChartAnimation<DonutDataPoint> = DefaultDonutAnimation()

    private lateinit var canvas: Canvas

    private var renderer: ChartContract.DonutRenderer =
        DonutChartRenderer(this, DonutNoAnimation())

    private val paint: Paint = Paint()

    private val configuration: DonutChartConfiguration
        get() =
            DonutChartConfiguration(
                width = measuredWidth,
                height = measuredHeight,
                paddings = Paddings(
                    paddingLeft.toFloat(),
                    paddingTop.toFloat(),
                    paddingRight.toFloat(),
                    paddingBottom.toFloat()
                ),
                thickness = donutThickness,
                total = donutTotal,
                colorsSize = donutColors.size,
                barBackgroundColor = donutBackgroundColor
            )

    init {
        setBackgroundColor(Color.TRANSPARENT)
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.DonutChartAttrs))
        handleEditMode()
    }

    override fun drawArc(degrees: List<Float>, innerFrame: Frame) {

        if (donutRoundCorners)
            paint.strokeCap = Paint.Cap.ROUND

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = donutThickness
        paint.isAntiAlias = true

        val invertedColors = donutColors.reversed() // Degrees list come inverted

        degrees.forEachIndexed { index, degree ->
            paint.color = invertedColors[index]
            canvas.drawArc(
                innerFrame.toRect().toRectF(),
                defaultStartAngle,
                degree,
                false,
                paint
            )
        }
    }

    override fun drawBackground(innerFrame: Frame) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = donutThickness
        paint.color = donutBackgroundColor
        paint.isAntiAlias = true

        val radius = (innerFrame.bottom - innerFrame.top) / 2
        canvas.drawCircle(
            innerFrame.left + radius,
            innerFrame.top + radius,
            radius,
            paint
        )
    }

    override fun drawDebugFrame(innerFrame: Frame) {
        canvas.drawRect(innerFrame.toRect(), paint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
    }

    fun show(values: List<Float>) {
        doOnPreDraw { renderer.preDraw(configuration) }
        renderer.render(values)
    }

    fun animate(values: List<Float>) {
        doOnPreDraw { renderer.preDraw(configuration) }
        renderer.anim(values, animation)
    }

    private fun handleEditMode() {
        if (isInEditMode) {
            show(editModeSampleData)
        }
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            donutThickness =
                getDimension(R.styleable.DonutChartAttrs_chart_donutThickness, donutThickness)
            donutBackgroundColor = getColor(
                R.styleable.DonutChartAttrs_chart_donutBackgroundColor,
                donutBackgroundColor
            )
            donutRoundCorners =
                getBoolean(R.styleable.DonutChartAttrs_chart_donutRoundCorners, donutRoundCorners)
            donutTotal = getFloat(R.styleable.DonutChartAttrs_chart_donutTotal, donutTotal)
            recycle()
        }
    }

    companion object {
        private const val defaultThickness = 50f
        private const val defaultColor = Color.BLACK
        private const val defaultBackgroundColor = Color.TRANSPARENT
        private const val defaultStartAngle = 90f
        private const val defaultDonutTotal = 100f
        private val editModeSampleData = listOf(70f)
    }
}