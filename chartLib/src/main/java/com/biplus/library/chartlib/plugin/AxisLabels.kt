package com.biplus.library.chartlib.plugin

import android.graphics.Canvas
import android.graphics.Paint
import com.biplus.library.chartlib.data.Label
import com.biplus.library.chartlib.helpers.Labels

class AxisLabels : Labels {
    override fun draw(canvas: Canvas, paint: Paint, xLabels: List<Label>) {
        xLabels.forEach {
            canvas.drawText(
                it.label,
                it.screenPositionX,
                it.screenPositionY,
                paint
            )
        }
    }
}