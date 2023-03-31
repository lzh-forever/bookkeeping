package com.example.bookkeeping.util

import android.util.Log
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Math.pow
import java.time.LocalDate
import java.util.UUID

//需要按照时间从早到晚排列
// XIRR现金流 投入为负 取出为正 更新资产为浮盈变化，不涉及投入资产变化，不用计算
// 第一笔更新资产视为投入，最后一笔更新资产视为取出

private const val TAG = "RateUtil"

suspend fun getXirrRate(accountId: UUID): Double {
    return getXirrRateAndAccountInfo(accountId).first
}

suspend fun getXirrRateAndAccountInfo(accountId: UUID): Triple<Double, Double, Double> {
    return getXirrRateAndAccountInfo(Repository.getRecordList(accountId))
}

//三元组返回值为 xirr内部收益率，account总资产，account净投入
suspend fun getXirrRateAndAccountInfo(list: List<Record>): Triple<Double, Double, Double> {
    Log.d(TAG, list.toString())
    return withContext(Dispatchers.Default) {
        val (cashFlow, totalAsset, netInvestment) = getXirrFlowAndAccountInfo(list)
        var rate = 0.0
        try {
            rate = xirr(cashFlow)
        } catch (e: Exception) {
            try {
                Log.d(TAG, e.toString())
                rate = xirrBisection(cashFlow)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
        Triple(rate, totalAsset, netInvestment)
    }
}


private fun getXirrFlowAndAccountInfo(list: List<Record>): Triple<List<Pair<LocalDate, Double>>, Double, Double> {
    if (list.isEmpty()) {
        return Triple(emptyList(), 0.0, 0.0)
    }
    val res = ArrayList<Pair<LocalDate, Double>>()
    var record: Record
    var lastAmountIndex = 0
    var netInvestment = 0.0
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
    netInvestment += record.amount
    //中间过程的转入转出
    for (i in 1 until lastAmountIndex) {
        record = list[i]
        when (record.type) {
            RecordType.TRANSFER_IN -> {
                res.add(record.date to record.amount * -1)
                netInvestment += record.amount
            }
            RecordType.TRANSFER_OUT -> {
                res.add(record.date to record.amount)
                netInvestment -= record.amount
            }
            RecordType.CURRENT_AMOUNT -> {
            }
        }
    }
    //当前资产
    record = list[lastAmountIndex]
    res.add(record.date to record.amount)
    val totalAsset: Double = record.amount
    return Triple(res, totalAsset, netInvestment)
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
    var high = 1000.0
    var guess: Double
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

//获取图标中展示的收益率（资金加权收益率）
suspend fun getModifiedDietzRate(list: List<Record>): List<Pair<LocalDate, Double>> {
    return withContext(Dispatchers.Default) {
        val res = ArrayList<Pair<LocalDate, Double>>()
        val startRecord = list[0]
        val vs = list[0].amount
        var index = 0

        for (r in list) {
            //当有更新总额时计算一个收益率结点
            if (r.type == RecordType.CURRENT_AMOUNT) {

                val t = r.date.toEpochDay() - startRecord.date.toEpochDay()
                //起始点
                if (t == 0L) {
                    res.add(startRecord.date to 0.0)
                    index++
                    continue
                }
                val ve = r.amount
                var weightedCost = 0.0
                var cost = 0.0
                //获取中间现金流
                for (i in 1 until index) {
                    val record = list[i]
                    if (record.type == RecordType.CURRENT_AMOUNT) {
                        continue
                    }
                    val weight = (r.date.toEpochDay() - record.date.toEpochDay()) * 1.0 / t
                    val c = getTransferRecordAmount(record)
                    cost += c
                    weightedCost += c * weight
                }
                //计算收益率
                val rate = (ve - vs - cost) / (vs + weightedCost)
                res.add(r.date to rate)
            }
            index++
        }
        res
    }
}

fun getTransferRecordAmount(record: Record): Double {
    val sign = when (record.type) {
        RecordType.TRANSFER_IN -> 1
        RecordType.TRANSFER_OUT -> -1
        RecordType.CURRENT_AMOUNT -> 0
    }
    return record.amount * sign
}

