package com.biplus.library.chartlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.biplus.library.chart.getTimeInMillisFirstDayOfWeek
import com.biplus.library.chart.getTimeInMillisLastDayOfWeek
import java.util.*

class WeeklyChartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseChartView(context, attrs, defStyleAttr) {
    private var calendar: Calendar = Calendar.getInstance()
    private var startTime = calendar.getTimeInMillisFirstDayOfWeek()
    private var endTime = calendar.getTimeInMillisLastDayOfWeek()
    private val items = mutableListOf<Pair<Pair<Long, Long>, Int>>()

    fun setTimes(list: List<Pair<Pair<Long, Long>, Int>>) {
        items.clear()
        items.addAll(list)
        postInvalidate()
    }

    private val mLineVerticalPaint: Paint by lazy {
        Paint().apply {
            color = Color.parseColor("#FF8040")
            strokeWidth = 20f
        }
    }

    override fun onDrawVertical(canvas: Canvas) {
        this.items.map {
            val first = it.first
            val hValue = it.second

            val startTime = first.first
            val endTime = first.second

            val wTime = endTime - startTime
            val wTimeW = this.endTime - this.startTime
            val percent = wTimeW / wTime
            val startX = mMaxWidth / percent
//            canvas.drawLine(startX, startYItem, startX, mMaxHeight, mLineVerticalPaint)
        }
    }

    override val listLabelBottom: List<Pair<String, Int>>
        get() = listOf(
                "Sun" to 8,
                "Mon" to 2,
                "Tues" to 3,
                "Wed" to 4,
                "Thurs" to 5,
                "Fri" to 6,
                "Sat" to 7,
        )
}