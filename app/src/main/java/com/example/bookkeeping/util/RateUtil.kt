package com.example.bookkeeping.util

import android.util.Log
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Math.pow
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

//需要按照时间从早到晚排列
// XIRR现金流 投入为负 取出为正 更新资产为浮盈变化，不涉及投入资产变化，不用计算
// 第一笔更新资产视为投入，最后一笔更新资产视为取出

private const val TAG = "RateUtil"

suspend fun getXirrRate(accountId:UUID): Double {
    return getXirrRate(Repository.getRecordList(accountId))
}

suspend fun getXirrRate(list: List<Record>): Double {
    Log.d(TAG, list.toString())
    return withContext(Dispatchers.Default) {
        val cashFlow = getXirrFlow(list)
        var res = 0.0
        try {
            res = xirr(cashFlow)
        } catch (e: Exception) {
            try {
                Log.d(TAG, e.toString())
                res = xirrBisection(cashFlow)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
        res
    }
}


private fun getXirrFlow(list: List<Record>): List<Pair<LocalDate, Double>> {
    if (list.isEmpty()) {
        return emptyList()
    }
    val res = ArrayList<Pair<LocalDate, Double>>()
    var record: Record
    var lastAmountIndex = 0
    for (i in list.lastIndex downTo 0) {
        record = list[i]
        if (record.type.isTransferType()) {
            continue
        }
        lastAmountIndex = i
        break
    }
    //初始资产
    record = list[0]
    res.add(record.date to record.amount * -1)
    //中间过程的转入转出
    for (i in 1 until lastAmountIndex) {
        record = list[i]
        when (record.type) {
            RecordType.TRANSFER_IN -> {
                res.add(record.date to record.amount * -1)

            }
            RecordType.TRANSFER_OUT -> {
                res.add(record.date to record.amount)

            }
            RecordType.CURRENT_AMOUNT -> {
            }
        }
    }
    //当前资产
    record = list[lastAmountIndex]
    res.add(record.date to record.amount)
    return res
}

fun xirr(cashFlows: List<Pair<LocalDate, Double>>, guess: Double = 0.1): Double {
    var x0 = guess
    val epsilon = 0.00001
    var count = 0

    while (count < 1000) {
        var f = 0.0
        var df = 0.0
        val lastDate = cashFlows.last().first.toEpochDay()
        for (cashFlow in cashFlows) {
            val t = (lastDate - cashFlow.first.toEpochDay()) / 365.0
            f += cashFlow.second * pow(1.0 + x0, t)
            df += cashFlow.second * t * pow(1.0 + x0, t - 1.0)
        }
        val x1 = x0 - f / df
        if (Math.abs(x1 - x0) < epsilon) {
            return x1
        }
        x0 = x1
        count++
    }
    throw Exception("cannot solve xirr with Newton")
}


fun xirrBisection(cashFlows: List<Pair<LocalDate, Double>>): Double {
    var low = -1.0
    var high = 1.0
    var guess = 0.0
    val epsilon = 0.00001
    var count = 0

    while (count < 1000) {
        guess = (low + high) / 2.0
        var f = 0.0
        val lastDate = cashFlows.last().first.toEpochDay()
        for (cashFlow in cashFlows) {
            val t = (lastDate - cashFlow.first.toEpochDay()) / 365.0
            f += cashFlow.second * pow(1.0 + guess, t)
        }
        if (Math.abs(f) < epsilon) {
            return guess
        } else if (f > 0.0) {
            low = guess
        } else {
            high = guess
        }
        count++
    }
    throw RuntimeException("cannot solve xirr with bisection")
}
