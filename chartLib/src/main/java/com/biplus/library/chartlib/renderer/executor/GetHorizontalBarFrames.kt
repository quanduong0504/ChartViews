package com.biplus.library.chartlib.renderer.executor

import com.biplus.library.chartlib.data.DataPoint
import com.biplus.library.chartlib.data.Frame

class GetHorizontalBarFrames {

    operator fun invoke(
        innerFrame: Frame,
        zeroPositionX: Float,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (data.size + 1) * spacingBetweenBars) /
                data.size / 2

        return data.map {
            Frame(
                left = zeroPositionX,
                top = it.screenPositionY - halfBarWidth,
                right = it.screenPositionX,
                bottom = it.screenPositionY + halfBarWidth
            )
        }
    }
}