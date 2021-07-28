package com.biplus.library.chartlib.renderer

import com.biplus.library.chartlib.animation.ChartAnimation
import com.biplus.library.chartlib.data.*
import com.biplus.library.chartlib.data.configuration.ChartConfiguration
import com.biplus.library.chartlib.data.configuration.LineChartConfiguration
import com.biplus.library.chartlib.data.configuration.toOuterFrame
import com.biplus.library.chartlib.extensions.maxValueBy
import com.biplus.library.chartlib.extensions.toDataPoints
import com.biplus.library.chartlib.extensions.toLabels
import com.biplus.library.chartlib.extensions.toScale
import com.biplus.library.chartlib.helpers.ChartContract
import com.biplus.library.chartlib.helpers.Painter
import com.biplus.library.chartlib.renderer.executor.DebugWithLabelsFrame
import com.biplus.library.chartlib.renderer.executor.DefineDataPointsClickableFrames
import com.biplus.library.chartlib.renderer.executor.DefineVerticalTouchableFrames
import com.biplus.library.chartlib.renderer.executor.MeasureLineChartPaddings

class LineChartRenderer(
    private val view: ChartContract.LineView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>
) : ChartContract.Renderer {

    private var data = emptyList<DataPoint>()

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: LineChartConfiguration

    private lateinit var xLabels: List<Label>

    private lateinit var yLabels: List<Label>

    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true

        this.chartConfiguration = configuration as LineChartConfiguration

        if (chartConfiguration.scale.notInitialized())
            chartConfiguration = chartConfiguration.copy(scale = data.toScale())

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

        val paddings = MeasureLineChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            longestLabelWidth = longestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.labelsPaddingToInnerChart,
            lineThickness = chartConfiguration.lineThickness,
            pointsDrawableWidth = chartConfiguration.pointsDrawableWidth,
            pointsDrawableHeight = chartConfiguration.pointsDrawableHeight
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        placeLabelsX(innerFrame)
        placeLabelsY(innerFrame)
        placeDataPoints(innerFrame)

        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }

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

        if (chartConfiguration.fillColor != 0 ||
            chartConfiguration.gradientFillColors.isNotEmpty()
        )
            view.drawLineBackground(innerFrame, data)

        view.drawLine(data)

        if (chartConfiguration.pointsDrawableWidth != -1)
            view.drawPoints(data)

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
                    DefineDataPointsClickableFrames()(
                        innerFrame = innerFrame,
                        datapointsCoordinates = data.map {
                            Pair(
                                it.screenPositionX,
                                it.screenPositionY
                            )
                        },
                        clickableRadius = chartConfiguration.clickableRadius
                    )
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
            DefineDataPointsClickableFrames()(
                innerFrame,
                data.map { Pair(it.screenPositionX, it.screenPositionY) },
                chartConfiguration.clickableRadius
            ).indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    override fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float> {

        if (x == null || y == null)
            return Triple(-1, -1f, -1f)

        val index =
            DefineVerticalTouchableFrames()(
                innerFrame,
                data.map { Pair(it.screenPositionX, it.screenPositionY) }
            )
                .indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    private fun placeLabelsX(innerFrame: Frame) {

        val labelsLeftPosition =
            innerFrame.left +
                painter.measureLabelWidth(xLabels.first().label, chartConfiguration.labelsSize) / 2
        val labelsRightPosition =
            innerFrame.right -
                painter.measureLabelWidth(xLabels.last().label, chartConfiguration.labelsSize) / 2
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
                innerFrame.left -
                    RendererConstants.labelsPaddingToInnerChart -
                    painter.measureLabelWidth(label.label, chartConfiguration.labelsSize) / 2
            label.screenPositionY = labelsBottomPosition - heightBetweenLabels * index
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        val scaleSize = chartConfiguration.scale.size
        val chartHeight = innerFrame.bottom - innerFrame.top
        val widthBetweenLabels = (innerFrame.right - innerFrame.left) / (xLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX = innerFrame.left + (widthBetweenLabels * index)
            dataPoint.screenPositionY =
                innerFrame.bottom -
                    (chartHeight * (dataPoint.value - chartConfiguration.scale.min) / scaleSize)
        }
    }
}