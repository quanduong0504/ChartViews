package com.biplus.library.chartlib.data.configuration

import com.biplus.library.chartlib.data.Paddings

data class DonutChartConfiguration(
    val width: Int,
    val height: Int,
    val paddings: Paddings,
    val thickness: Float,
    val total: Float,
    val colorsSize: Int,
    val barBackgroundColor: Int
)