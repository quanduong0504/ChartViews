package com.biplus.library.chartlib.animation

import com.biplus.library.chartlib.data.DataPoint

class NoAnimation : ChartAnimation<DataPoint>() {

    override fun animateFrom(
        startPosition: Float,
        entries: List<DataPoint>,
        callback: () -> Unit
    ): ChartAnimation<DataPoint> = this
}