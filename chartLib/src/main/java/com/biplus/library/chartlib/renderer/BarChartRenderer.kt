package com.biplus.library.chartlib.renderer

import com.biplus.library.chartlib.animation.ChartAnimation
import com.biplus.library.chartlib.data.*
import com.biplus.library.chartlib.data.configuration.BarChartConfiguration
import com.biplus.library.chartlib.data.configuration.ChartConfiguration
import com.biplus.library.chartlib.data.configuration.toOuterFrame
import com.biplus.library.chartlib.extensions.maxValueBy
import com.biplus.library.chartlib.extensions.toBarScale
import com.biplus.library.chartlib.extensions.toDataPoints
import com.biplus.library.chartlib.extensions.toLabels
import com.biplus.library.chartlib.helpers.ChartContract
import com.biplus.library.chartlib.helpers.Painter
import com.biplus.library.chartlib.renderer.executor.*

class BarChartRenderer(
    private val view: ChartContract.BarView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>
) : ChartContract.Renderer {

    private var data = emptyList<DataPoint>()

    private var zeroPositionY: Float = 0.0f

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: BarChartConfiguration

    private lateinit var xLabels: List<Label>

    private lateinit var yLabels: List<Label>

    private lateinit var barsBackgroundFrames: List<Frame>

    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true

        chartConfiguration = configuration as BarChartConfiguration

        if (chartConfiguration.scale.notInitialized())
            chartConfiguration = chartConfiguration.copy(scale = data.toBarScale())

        xLabels = data.toLabels()
        val scaleStep = chartConfiguration.scale.size / RendererConstants.defaultScaleNumberOfSteps
        yLabels = List(RendererConstants.defaultScaleNumberOfSteps + 1) {
            val scaleValue = chartConfiguration.scale.min + scaleStep * it
            Label(
                label = chartConfiguration.labelsFormatter(scaleValue),
                screenPositionX = 0F,
                screenPositionY = 0F
            )
        }

        val longestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    it.label,
                    chartConfiguration.labelsSize
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureBarChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            longestLabelWidth = longestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.labelsPaddingToInnerChart
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        zeroPositionY =
            processZeroPositionY(
                innerTop = innerFrame.top,
                innerBottom = innerFrame.bottom,
                scaleRange = chartConfiguration.scale.size
            )

        placeLabelsX(innerFrame)
        placeLabelsY(innerFrame)
        placeDataPoints(innerFrame, zeroPositionY)

        barsBackgroundFrames =
            GetVerticalBarBackgroundFrames()(
                innerFrame,
                chartConfiguration.barsSpacing,
                data
            )

        animation.animateFrom(zeroPositionY, data) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (data.isEmpty()) return

        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabels(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabels(yLabels)

        view.drawGrid(
            innerFrame,
            xLabels.map { it.screenPositionX },
            yLabels.map { it.screenPositionY }
        )

        if (chartConfiguration.barsBackgroundColor != -1)
            view.drawBarsBackground(barsBackgroundFrames)

        view.drawBars(
            GetVerticalBarFrames()(
                innerFrame,
                zeroPositionY,
                chartConfiguration.barsSpacing,
                data
            )
        )

        if (RendererConstants.inDebug) {
            view.drawDebugFrame(
                listOf(outerFrame, innerFrame) +
                    DebugWithLabelsFrame()(
                        painter = painter,
                        axisType = chartConfiguration.axis,
                        xLabels = xLabels,
                        yLabels = yLabels,
                        labelsSize = chartConfiguration.labelsSize
                    ) +
                    DefineVerticalBarsClickableFrames()(
                        innerFrame,
                        data.map { Pair(it.screenPositionX, it.screenPositionY) }
                    ) +
                    Frame(innerFrame.left, zeroPositionY, innerFrame.right, zeroPositionY)
            )
        }
    }

    override fun render(entries: List<Pair<String, Float>>) {
        data = entries.toDataPoints()
        view.postInvalidate()
    }

    override fun anim(entries: List<Pair<String, Float>>, animation: ChartAnimation<DataPoint>) {
        data = entries.toDataPoints()
        this.animation = animation
        view.postInvalidate()
    }

    override fun processClick(x: Float?, y: Float?): Triple<Int, Float, Float> {

        if (x == null || y == null || data.isEmpty())
            return Triple(-1, -1f, -1f)

        val index =
            DefineVerticalBarsClickableFrames()(
                innerFrame,
                data.map { Pair(it.screenPositionX, it.screenPositionY) }
            )
                .indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    override fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float> = processClick(x, y)

    private fun placeLabelsX(innerFrame: Frame) {

        val halfBarWidth = (innerFrame.right - innerFrame.left) / xLabels.size / 2
        val labelsLeftPosition = innerFrame.left + halfBarWidth
        val labelsRightPosition = innerFrame.right - halfBarWidth
        val widthBetweenLabels = (labelsRightPosition - labelsLeftPosition) / (xLabels.size - 1)
        val xLabelsVerticalPosition =
            innerFrame.bottom -
                painter.measureLabelAscent(chartConfiguration.labelsSize) +
                RendererConstants.labelsPaddingToInnerChart

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = labelsLeftPosition + (widthBetweenLabels * index)
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(innerFrame: Frame) {

        val heightBetweenLabels =
            (innerFrame.bottom - innerFrame.top) / RendererConstants.defaultScaleNumberOfSteps
        val labelsBottomPosition =
            innerFrame.bottom + painter.measureLabelHeight(chartConfiguration.labelsSize) / 2

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                innerFrame.right -
                    RendererConstants.labelsPaddingToInnerChart -
                    painter.measureLabelWidth(label.label, chartConfiguration.labelsSize) / 2
            label.screenPositionY = labelsBottomPosition - heightBetweenLabels * index
        }
    }

    private fun placeDataPoints(
        innerFrame: Frame,
        zeroPositionY: Float
    ) {
        // Chart upper part with positive points
        val positiveHeight = zeroPositionY - innerFrame.top
        val positiveScale = chartConfiguration.scale.max

        // Chart bottom part with negative points
        val negativeHeight = innerFrame.bottom - zeroPositionY
        val negativeScale = chartConfiguration.scale.min

        val halfBarWidth = (innerFrame.right - innerFrame.left) / xLabels.size / 2
        val labelsLeftPosition = innerFrame.left + halfBarWidth
        val labelsRightPosition = innerFrame.right - halfBarWidth
        val widthBetweenLabels = (labelsRightPosition - labelsLeftPosition) / (xLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX = labelsLeftPosition + (widthBetweenLabels * index)
            dataPoint.screenPositionY =
                if (dataPoint.value >= 0f)
                    zeroPositionY - (positiveHeight * dataPoint.value / positiveScale) // Positive
                else zeroPositionY + (negativeHeight * dataPoint.value / negativeScale) // Negative
        }
    }

    private fun processZeroPositionY(
        innerTop: Float,
        innerBottom: Float,
        scaleRange: Float
    ): Float {
        val chartHeight = innerBottom - innerTop
        return innerTop + (chartHeight * chartConfiguration.scale.max / scaleRange)
    }
}