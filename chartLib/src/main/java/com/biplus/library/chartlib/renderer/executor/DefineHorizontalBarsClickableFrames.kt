package com.biplus.library.chartlib.renderer.executor

import com.biplus.library.chartlib.data.Frame

class DefineHorizontalBarsClickableFrames {

    operator fun invoke(
        innerFrame: Frame,
        datapointsCoordinates: List<Pair<Float, Float>>
    ): List<Frame> {

        val halfDistanceBetweenDataPoints =
            (innerFrame.bottom - innerFrame.top - (datapointsCoordinates.size + 1)) /
                datapointsCoordinates.size / 2

        return datapointsCoordinates.map {
            Frame(
                left = innerFrame.left,
                top = it.second - halfDistanceBetweenDataPoints,
                right = innerFrame.right,
                bottom = it.second + halfDistanceBetweenDataPoints
            )
        }
    }
}