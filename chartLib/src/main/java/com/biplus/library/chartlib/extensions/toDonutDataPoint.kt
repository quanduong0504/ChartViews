package com.biplus.library.chartlib.extensions

import com.biplus.library.chartlib.data.DonutDataPoint

fun Float.toDonutDataPoint(offset: Float): DonutDataPoint = DonutDataPoint(this + offset)