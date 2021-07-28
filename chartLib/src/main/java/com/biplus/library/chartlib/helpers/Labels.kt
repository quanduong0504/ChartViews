package com.biplus.library.chartlib.helpers

import android.graphics.Canvas
import android.graphics.Paint
import com.biplus.library.chartlib.data.Label

interface Labels {
    fun draw(canvas: Canvas, paint: Paint, xLabels: List<Label>)
}