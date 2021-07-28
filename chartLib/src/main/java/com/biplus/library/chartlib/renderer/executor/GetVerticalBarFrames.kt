package com.biplus.library.chartlib.renderer.executor

import com.biplus.library.chartlib.data.DataPoint
import com.biplus.library.chartlib.data.Frame

class GetVerticalBarFrames {

    operator fun invoke(
        innerFrame: Frame,
        zeroPositionY: Float,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.right - innerFrame.left - (data.size + 1) * spacingBetweenBars) /
                data.size / 2

        return data.map {
            Frame(
                left = it.screenPositionX - halfBarWidth,
                top = it.screenPositionY,
                right = it.screenPositionX + halfBarWidth,
                bottom = zeroPositionY
            )
        }
    }
}