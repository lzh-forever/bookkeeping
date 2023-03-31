package com.example.bookkeeping.util

import java.time.LocalDate

fun getChartData(list: List<Pair<LocalDate, Double>>): List<Pair<LocalDate, Double>> {
    if (list.isEmpty()) {
        return emptyList()
    }
    val map = HashMap<LocalDate, Double>()
    val sundayList = getSundayList(list.first().first, list.last().first)
    var index = 0
    var rate = list[index].second
    for (sunday in sundayList) {
        var curDate = list[index].first
        while (curDate.inSameWeek(sunday)) {
            rate = list[index].second
            if (index == list.lastIndex) {
                break
            }
            index++
            curDate = list[index].first
        }
        map[sunday] = rate
    }
    return map.map { Pair(it.key, it.value) }.sortedBy { it.first }
}


