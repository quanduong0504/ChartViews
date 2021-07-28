package com.biplus.library.chartlib.extensions

import android.content.res.Resources

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()