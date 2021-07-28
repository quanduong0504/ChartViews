package com.biplus.library.chartlib.helpers

import android.view.ViewGroup

interface Tooltip {
    fun onCreateTooltip(parentView: ViewGroup)
    fun onDataPointTouch(x: Float, y: Float)
    fun onDataPointClick(x: Float, y: Float)
}