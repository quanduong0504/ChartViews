package com.biplus.library.chart

import java.util.*

internal fun Calendar.backWeek() {
    addField(Calendar.WEEK_OF_MONTH, -1)
    this.clear(Calendar.MINUTE)
    this.clear(Calendar.MINUTE)
    this.clear(Calendar.SECOND)
    this.clear(Calendar.MILLISECOND)
}
internal fun Calendar.backMonth() : Calendar {
    addField(Calendar.MONTH, -1)
    return this
}
internal fun Calendar.backYear() {
    this.add(Calendar.YEAR, -1)
}
internal fun Calendar.setMonth(year: Int, monthOfYear: Int) {
    setField(Calendar.YEAR, year)
    setField(Calendar.MONTH, monthOfYear)
}
internal fun Calendar.setYear(year: Int) {
    setField(Calendar.YEAR, year)
}
internal fun Calendar.setNormal(year: Int, month: Int, dayOfMonth: Int) {
    set(year, month, dayOfMonth)
}
internal fun Calendar.setWeek(year: Int, monthOfYear: Int, dayOfMonth: Int) {
    setMonth(year, monthOfYear)
    setField(Calendar.DAY_OF_MONTH, dayOfMonth)
}
internal fun Calendar.nextWeek() {
    addField(Calendar.WEEK_OF_MONTH, 1)
    this.clear(Calendar.MINUTE)
    this.clear(Calendar.MINUTE)
    this.clear(Calendar.SECOND)
    this.clear(Calendar.MILLISECOND)
}
internal fun Calendar.nextMonth() {
    addField(Calendar.MONTH, 1)
}
internal fun Calendar.nextYear() {
    this.add(Calendar.YEAR, 1)
}
internal fun Calendar.getTimeInMillisFirstDayOfWeek() : Long {
    val cal = copyCal()
    cal.setField(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    return cal.timeInMillis
}
internal fun Calendar.getTimeInMillisLastDayOfWeek() : Long {
    val cal = copyCal()
    cal.setField(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    return cal.timeInMillis
}
internal fun Calendar.getTimeInMillisFirstDay() : Long {
    val cal = copyCal()
    cal.setField(Calendar.HOUR_OF_DAY, 0)
    return cal.timeInMillis
}
internal fun Calendar.getTimeInMillisLastDay() : Long {
    val cal = copyCal()
    cal.setField(Calendar.HOUR_OF_DAY, 24)
    return cal.timeInMillis
}
internal fun Calendar.getMonthFromToDate() : LongArray {
    val cal = copyCal()
    val fromDate = cal
            .addField(Calendar.MONTH, 0)
            .setField(Calendar.DAY_OF_MONTH, 1)
            .timeInMillis
    val toDate = cal
            .addField(Calendar.MONTH, 1)
            .setField(Calendar.DAY_OF_MONTH, 1)
            .timeInMillis
    return longArrayOf(fromDate, toDate)
}
internal fun Calendar.getYearFromToDate() : LongArray {
    val cal = copyCal()
    val fromDate = cal
            .addField(Calendar.YEAR, 0)
            .setField(Calendar.DAY_OF_YEAR, 1)
            .timeInMillis
    val toDate = cal
            .addField(Calendar.YEAR, 1)
            .setField(Calendar.DAY_OF_YEAR, 1)
            .timeInMillis
    return longArrayOf(fromDate, toDate)
}
private fun Calendar.addField(field: Int, value: Int) : Calendar {
    this.add(field, value)
    return this
}
internal fun Calendar.setField(field: Int, value: Int) : Calendar {
    this.set(field, value)
    return this
}
internal fun Calendar.copyCal() : Calendar {
    val cal = clone() as Calendar
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)
    return cal
}
internal fun Calendar.getYear() : Int = this.get(Calendar.YEAR)
internal fun Calendar.getMonth() : Int = this.get(Calendar.MONTH)
internal fun Calendar.getDayOfMonth() : Int = this.get(Calendar.DAY_OF_MONTH)