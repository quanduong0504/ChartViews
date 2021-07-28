package com.biplus.library.chartlib.renderer.executor

import com.biplus.library.chartlib.data.DataPoint
import com.biplus.library.chartlib.data.Frame

class GetHorizontalBarBackgroundFrames {

    operator fun invoke(
        innerFrame: Frame,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (data.size + 1) * spacingBetweenBars) /
                data.size / 2

        return data.map {
            Frame(
                left = innerFrame.left,
                top = it.screenPositionY - halfBarWidth,
                right = innerFrame.right,
                bottom = it.screenPositionY + halfBarWidth
            )
        }
    }
}