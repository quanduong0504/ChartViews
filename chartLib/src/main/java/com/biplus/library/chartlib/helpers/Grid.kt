package com.biplus.library.chartlib.helpers

import android.graphics.Canvas
import com.biplus.library.chartlib.data.Frame

interface Grid {
    fun draw(
        canvas: Canvas,
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    )
}