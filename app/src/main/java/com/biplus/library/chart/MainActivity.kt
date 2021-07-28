package com.biplus.library.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.biplus.library.chartlib.PointData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cal = Calendar.getInstance()
        cal.setField(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        val now = Calendar.getInstance()

        val start = now.copyCal()
        start.set(Calendar.HOUR_OF_DAY, 11)
        start.set(Calendar.MINUTE, 30)

        val end = now.copyCal()
        end.set(Calendar.HOUR_OF_DAY, 12)
        end.set(Calendar.MINUTE, 30)

        weekView.setData(listOf(
            PointData(start.timeInMillis, end.timeInMillis, 80),
            PointData(getTime(now.copyCal(), 6), getTime(now.copyCal(), 7), 120),
            PointData(getTime(now.copyCal(), 3), getTime(now.copyCal(), 4), 200),
            PointData(getTime(now.copyCal(), 5), getTime(now.copyCal(), 6), 100),
            PointData(getTime(now.copyCal(), 2), getTime(now.copyCal(), 3), 60),
        ))
    }

    private fun getTime(cal: Calendar, hours: Int) = cal.copyCal().setField(Calendar.HOUR_OF_DAY, hours).timeInMillis
}