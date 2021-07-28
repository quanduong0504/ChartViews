package com.biplus.library.chartlib.data

import com.biplus.library.chartlib.renderer.RendererConstants

data class Scale(val min: Float, val max: Float) {
    val size = max - min
}

fun Scale.notInitialized() = max == min && min == RendererConstants.notInitialized