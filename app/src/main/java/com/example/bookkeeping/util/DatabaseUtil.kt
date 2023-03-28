package com.example.bookkeeping.util

import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType

suspend fun updateAccountWhenInsertAmountRecord(
    account: Account, record: Record, latestAmountRecord: Record?
): Account? = if (latestAmountRecord == null) {
    //初次记账，更新净投入和资产
    account.copy(
        totalAsset = record.amount, netInvestment = record.amount,
        initDate = record.date, initAsset = record.amount, initId = record.id
    )
} else if (beforeComparedRecord(record, latestAmountRecord)) {
    //晚于上次总额记录，不用更新总资产
    //更新初始资产时需要更改净投入
    var resAccount: Account? = null
    if (record.date.isBefore(account.initDate)) {
        with(account) {
            val rate = getXirrRate(id)
            resAccount = copy(
                netInvestment = netInvestment - initAsset + record.amount, rate = rate,
                initDate = record.date, initAsset = record.amount, initId = record.id
            )
        }
    }
    resAccount
} else {
    //更新总额
    with(account) {
        val rate = getXirrRate(id)
        copy(totalAsset = record.amount, rate = rate)
    }
}

suspend fun updateAccountWhenInsertTransferRecord(
    account: Account, record: Record, latestAmountRecord: Record
): Account {
    val sign = if (record.type == RecordType.TRANSFER_IN) 1 else -1
    val amount = sign * record.amount
    var asset = account.totalAsset
    var netInvestment = account.netInvestment
    var recalculate = false

    if (beforeComparedRecord(record, latestAmountRecord)) {
        netInvestment += amount
        recalculate = true
    } else {
        asset += amount
        netInvestment += amount
    }
    val rate = if (recalculate) {
        getXirrRate(account.id)
    } else {
        account.rate
    }
    return account.copy(netInvestment = netInvestment, totalAsset = asset, rate = rate)
}

suspend fun updateAccountWhenUpdateTransferRecord(
    originalRecord: Record, record: Record, account: Account, latestAmountRecord: Record
): Account? {
    val sign1 = if (originalRecord.type == RecordType.TRANSFER_IN) 1 else -1
    val sign2 = if (record.type == RecordType.TRANSFER_IN) 1 else -1
    val amount1 = originalRecord.amount * sign1
    val amount2 = record.amount * sign2
    var asset = account.totalAsset
    var netInvestment = account.netInvestment
    var recalculate = false
    if (beforeComparedRecord(originalRecord, latestAmountRecord)) {
        //之前只影响净投入
        netInvestment -= amount1
        recalculate = true
    } else {
        //之后影响净投入和最新资产，但不用计算收益率
        asset -= amount1
        netInvestment -= amount1
    }
    if (beforeComparedRecord(record, latestAmountRecord)) {
        netInvestment += amount2
        recalculate = true
    } else {
        asset += amount2
        netInvestment += amount2
    }
    val rate = if (recalculate) {
        getXirrRate(account.id)
    } else {
        account.rate
    }
    if (netInvestment == account.netInvestment && asset == account.totalAsset && rate == account.rate) {
        return null
    }
    return account.copy(netInvestment = netInvestment, totalAsset = asset, rate = rate)
}


suspend fun updateAccountWhenUpdateAmountRecord(record: Record, account: Account): Account {
    //对于一个amount数值来讲，中间的没有作用，账户收益只由初始资产、中间转入转出，最后一次记录和之后的转入转出决定
    //每次更新amount日期后，会导致后面的的转入转出部分发生变化，这就需要对recordList进行读取
    //这里选择利用计算收益率时对recordList进行遍历直接计算出数据,然后更新初始资产数据
    val (rate, totalAsset, netInvestment) = getXirrRateAndAccountInfo(account.id)
    var initDate = account.initDate
    var initId = account.initId
    var initAsset = account.initAsset
    //转入转出不能超过初始，这里只能是amountRecord
    if (record.date.isBefore(account.initDate)) {
        with(record) {
            initDate = date
            initId = id
            initAsset = amount
        }
    }
    return account.copy(
        netInvestment = netInvestment, totalAsset = totalAsset, rate = rate,
        initDate = initDate, initAsset = initAsset, initId = initId
    )
}

fun beforeComparedRecord(record: Record, comparedRecord: Record): Boolean {
    return record.date.isBefore(comparedRecord.date) || (record.date.isEqual(comparedRecord.date) && record.updateTime < comparedRecord.updateTime)
}

