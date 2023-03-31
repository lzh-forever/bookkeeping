package com.example.bookkeeping.util

import java.time.LocalDate
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields

fun getSundayList(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
    val startSunday = startDate.getSunday()
    val endSunday = endDate.getSunday()
    val res = ArrayList<LocalDate>()
    var sunday = startSunday
    while (!sunday.isEqual(endSunday)) {
        res.add(sunday)
        sunday = sunday.plusDays(7)
    }
    res.add(endSunday)
    return res
}

fun LocalDate.getSunday(): LocalDate {
    val day = dayOfWeek.value
    val delta = 7L - day
    return plusDays(delta)
}

fun LocalDate.inSameWeek(localDate: LocalDate): Boolean {
    val weekField: TemporalField = WeekFields.ISO.weekOfWeekBasedYear()
    val week1 = get(weekField)
    val week2 = localDate.get(weekField)
    return week1 == week2 && year == localDate.year
}