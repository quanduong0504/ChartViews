package com.biplus.library.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        weekView.setTime(listOf(
                (start.timeInMillis to end.timeInMillis) to 80,
                (getTime(now.copyCal(), 6) to getTime(now.copyCal(), 7)) to 120,
                (getTime(now.copyCal(), 3) to getTime(now.copyCal(), 4)) to 200,
                (getTime(now.copyCal(), 5) to getTime(now.copyCal(), 6)) to 100,
                (getTime(now.copyCal(), 2) to getTime(now.copyCal(), 3)) to 60
        ))
    }

    private fun getTime(cal: Calendar, hours: Int) = cal.copyCal().setField(Calendar.HOUR_OF_DAY, hours).timeInMillis
}