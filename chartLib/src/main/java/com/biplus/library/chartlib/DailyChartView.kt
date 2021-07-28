package com.biplus.library.chartlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import com.biplus.library.chart.getTimeInMillisFirstDay
import com.biplus.library.chart.getTimeInMillisLastDay
import java.text.SimpleDateFormat
import java.util.*

class DailyChartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseChartView(context, attrs, defStyleAttr) {
    private var calendar: Calendar = Calendar.getInstance()
    private var startTime = calendar.getTimeInMillisFirstDay()
    private var endTime = calendar.getTimeInMillisLastDay()
    private val items = mutableListOf<Pair<Pair<Long, Long>, Int>>()
    private val mWidthOffset by lazy {
        mMaxWidth / (60 * 24f)
    }

    override val listLabelBottom: List<Pair<String, Int>>
        get() = listOf(
                "0:00" to 0,
                "04:00" to 4,
                "08:00" to 8,
                "12:00" to 12,
                "16:00" to 16,
                "20:00" to 20,
                "24:00" to 24
        )

    fun setTime(time: List<Pair<Pair<Long, Long>, Int>>) {
        this.items.clear()
        this.items.addAll(time)
        postInvalidate()
    }

    override fun onDrawVertical(canvas: Canvas) {
        this.items.map {
            val time = it.first
            val startTime = time.first
            val endTime = time.second

            val width = ((endTime - startTime) / 60000) * mWidthOffset
            val paint = Paint().apply {
                color = Color.parseColor("#FF8040")
            }
            val startX = calculatorStartX(it.first)
            val startY = calculatorStartY(it.second) + dp2px(TOTAL_MARGIN_TOP)
            val rectF = RectF(startX, startY,
                    startX + width - dp2px(2f),
                    mQuadrant.quadrantPaddingEndY + dp2px(LINE_MARGIN_TOP))

            val dpRadius = dp2px(16f).toFloat()
            canvas.drawRoundRect(rectF, dpRadius, dpRadius, paint)
            canvas.drawText(it.second.toString(), startX + (width / 2), startY - dp2px(5f), Paint().apply {
                color = Color.BLACK
                textSize = dp2px(10f).toFloat()
                textAlign = Paint.Align.CENTER
            })
//            canvas.drawText("${startTime.toDate()} - ${endTime.toDate()}", startX + (width / 2), startY - dp2px(15f), Paint().apply {
//                color = Color.BLACK
//                textSize = dp2px(10f).toFloat()
//                textAlign = Paint.Align.CENTER
//            })
        }
    }

    private fun Long.toDate() = SimpleDateFormat("hh:mm").format(this).toString()

    private fun calculatorStartY(itemValue: Int) : Float {
        Log.d("checkResultMaxHeight", "MH: $mMaxHeight")
        return mHeightOffset * ((mMaxPlace - itemValue))
    }

    private fun calculatorStartX(time: Pair<Long, Long>) : Float {
        val startTime = time.first
        val mStart = this.endTime - startTime
        val wStart = (60 * 24 - (mStart / 60000)) * mWidthOffset

        return startXItem + wStart + dp2px(1f)
    }
}