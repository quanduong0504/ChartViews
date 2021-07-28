package com.biplus.library.chartlib.animation

import com.biplus.library.chartlib.data.DonutDataPoint

class DonutNoAnimation : ChartAnimation<DonutDataPoint>() {

    override fun animateFrom(
        startPosition: Float,
        entries: List<DonutDataPoint>,
        callback: () -> Unit
    ): ChartAnimation<DonutDataPoint> = this
}