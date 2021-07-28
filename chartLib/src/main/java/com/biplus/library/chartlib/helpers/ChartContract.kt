package com.biplus.library.chartlib.helpers

import com.biplus.library.chartlib.animation.ChartAnimation
import com.biplus.library.chartlib.data.DataPoint
import com.biplus.library.chartlib.data.DonutDataPoint
import com.biplus.library.chartlib.data.Frame
import com.biplus.library.chartlib.data.Label
import com.biplus.library.chartlib.data.configuration.ChartConfiguration
import com.biplus.library.chartlib.data.configuration.DonutChartConfiguration

interface ChartContract {

    interface AxisView {
        fun postInvalidate()
        fun drawLabels(xLabels: List<Label>)
        fun drawGrid(
            innerFrame: Frame,
            xLabelsPositions: List<Float>,
            yLabelsPositions: List<Float>
        )

        fun drawDebugFrame(frames: List<Frame>)
    }

    interface LineView : AxisView {
        fun drawLine(points: List<DataPoint>)
        fun drawLineBackground(innerFrame: Frame, points: List<DataPoint>)
        fun drawPoints(points: List<DataPoint>)
    }

    interface BarView : AxisView {
        fun drawBars(frames: List<Frame>)
        fun drawBarsBackground(frames: List<Frame>)
    }

    interface DonutView {
        fun postInvalidate()
        fun drawArc(degrees: List<Float>, innerFrame: Frame)
        fun drawBackground(innerFrame: Frame)
        fun drawDebugFrame(innerFrame: Frame)
    }

    interface Renderer {
        fun preDraw(configuration: ChartConfiguration): Boolean
        fun draw()
        fun render(entries: List<Pair<String, Float>>)
        fun anim(entries: List<Pair<String, Float>>, animation: ChartAnimation<DataPoint>)
        fun processClick(x: Float?, y: Float?): Triple<Int, Float, Float>
        fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float>
    }

    interface DonutRenderer {
        fun preDraw(configuration: DonutChartConfiguration): Boolean
        fun draw()
        fun render(values: List<Float>)
        fun anim(values: List<Float>, animation: ChartAnimation<DonutDataPoint>)
    }
}