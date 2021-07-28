package com.biplus.library.chartlib.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.biplus.library.chartlib.R
import com.biplus.library.chartlib.animation.DefaultHorizontalAnimation
import com.biplus.library.chartlib.animation.NoAnimation
import com.biplus.library.chartlib.data.*
import com.biplus.library.chartlib.data.configuration.BarChartConfiguration
import com.biplus.library.chartlib.data.configuration.ChartConfiguration
import com.biplus.library.chartlib.extensions.obtainStyledAttributes
import com.biplus.library.chartlib.helpers.ChartContract
import com.biplus.library.chartlib.renderer.HorizontalBarChartRenderer

class HorizontalBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AxisChartView(context, attrs, defStyleAttr), ChartContract.BarView {

    @Suppress("MemberVisibilityCanBePrivate")
    var spacing = defaultSpacing

    @ColorInt
    @Suppress("MemberVisibilityCanBePrivate")
    var barsColor: Int = defaultBarsColor

    @Suppress("MemberVisibilityCanBePrivate")
    var barRadius: Float = defaultBarsRadius

    @Suppress("MemberVisibilityCanBePrivate")
    var barsBackgroundColor: Int = -1

    override val chartConfiguration: ChartConfiguration
        get() = BarChartConfiguration(
            measuredWidth,
            measuredHeight,
            Paddings(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                paddingRight.toFloat(),
                paddingBottom.toFloat()
            ),
            axis,
            labelsSize,
            scale,
            labelsFormatter,
            barsBackgroundColor,
            spacing
        )

    init {
        renderer = HorizontalBarChartRenderer(this, painter, NoAnimation())
        animation = DefaultHorizontalAnimation()
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.BarChartAttrs))
        handleEditMode()
    }

    override fun drawBars(frames: List<Frame>) {
        painter.prepare(color = barsColor, style = Paint.Style.FILL)
        frames.forEach {
            canvas.drawRoundRect(
                it.toRectF(),
                barRadius,
                barRadius,
                painter.paint
            )
        }
    }

    override fun drawBarsBackground(frames: List<Frame>) {
        painter.prepare(color = barsBackgroundColor, style = Paint.Style.FILL)
        frames.forEach {
            canvas.drawRoundRect(
                it.toRectF(),
                barRadius,
                barRadius,
                painter.paint
            )
        }
    }

    override fun drawLabels(xLabels: List<Label>) {
        painter.prepare(textSize = labelsSize, color = labelsColor, font = labelsFont)
        labels.draw(canvas, painter.paint, xLabels)
    }

    override fun drawGrid(
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    ) {
        grid.draw(canvas, innerFrame, xLabelsPositions, yLabelsPositions)
    }

    override fun drawDebugFrame(frames: List<Frame>) {
        painter.prepare(color = -0x1000000, style = Paint.Style.STROKE)
        frames.forEach { canvas.drawRect(it.toRect(), painter.paint) }
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            spacing = getDimension(R.styleable.BarChartAttrs_chart_spacing, spacing)
            barsColor = getColor(R.styleable.BarChartAttrs_chart_barsColor, barsColor)
            barRadius = getDimension(R.styleable.BarChartAttrs_chart_barsRadius, barRadius)
            barsBackgroundColor =
                getColor(R.styleable.BarChartAttrs_chart_barsBackgroundColor, barsBackgroundColor)
            recycle()
        }
    }

    companion object {
        private const val defaultSpacing = 10f
        private const val defaultBarsColor = -0x1000000
        private const val defaultBarsRadius = 0F
    }
}