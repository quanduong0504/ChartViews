package com.biplus.library.chartlib.data

data class DataPoint(
    val label: String,
    val value: Float,
    var screenPositionX: Float = 0f,
    var screenPositionY: Float = 0f
)