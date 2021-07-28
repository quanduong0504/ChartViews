package com.biplus.library.chartlib.data.configuration

import com.biplus.library.chartlib.data.AxisType
import com.biplus.library.chartlib.data.Frame
import com.biplus.library.chartlib.data.Paddings
import com.biplus.library.chartlib.data.Scale

open class ChartConfiguration(
    open val width: Int,
    open val height: Int,
    open val paddings: Paddings,
    open val axis: AxisType,
    open val labelsSize: Float,
    open val scale: Scale,
    open val labelsFormatter: (Float) -> String
)

internal fun ChartConfiguration.toOuterFrame(): Frame {
    return Frame(
        left = paddings.left,
        top = paddings.top,
        right = width - paddings.right,
        bottom = height - paddings.bottom
    )
}