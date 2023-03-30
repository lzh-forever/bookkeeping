package com.example.bookkeeping.util

import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType

//latest指插入前的上一个，这里主要用于判断初始资产，插入后自己不早于自己，前后都一样
//增加accountInit信息后也可以利用init信息判断，这样插入前后的latest都可以使用
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

//transfer类型不影响LastAmount,前后都不影响
suspend fun updateAccountWhenInsertTransferRecord(
    account: Account, record: Record, latestAmountRecord: Record
): Account {
    val amount = getTransferRecordAmount(record)
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

//transfer类型不影响LastAmount,前后都不影响
suspend fun updateAccountWhenUpdateTransferRecord(
    originalRecord: Record, record: Record, account: Account, latestAmountRecord: Record
): Account? {
    val amount1 = getTransferRecordAmount(originalRecord)
    val amount2 = getTransferRecordAmount(record)
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

//如果是删除前的最后，早于最后一个不影响，晚于即自己是最后，需要计算
//如果是删除后的最后：
//1.删除的是自己，那么肯定晚于现在的最后，需要计算 2.删除的不是自己，最后一个和删除前一样
//查找的顺序不影响
suspend fun updateAccountWhenDeleteAmountRecord(
    record: Record,
    account: Account,
    latestAmountRecord: Record
): Account? {
    //初始资产不可删除，中间资产删除不影响，后面的资产删除后可能影响转入转出
    if (beforeComparedRecord(record, latestAmountRecord)) {
        return null
    }
    val (rate, totalAsset, netInvestment) = getXirrRateAndAccountInfo(account.id)
    return account.copy(netInvestment = netInvestment, totalAsset = totalAsset, rate = rate)
}


//transfer类型不影响LastAmount,前后都不影响
suspend fun updateAccountWhenDeleteTransferRecord(
    record: Record, account: Account, latestAmountRecord: Record
): Account {
    val amount = getTransferRecordAmount(record)
    var asset = account.totalAsset
    var netInvestment = account.netInvestment
    var recalculate = false

    if (beforeComparedRecord(record, latestAmountRecord)) {
        //之前只影响净投入
        netInvestment -= amount
        recalculate = true
    } else {
        //之后影响净投入和最新资产，但不用计算收益率
        asset -= amount
        netInvestment -= amount
    }
    val rate = if (recalculate) {
        getXirrRate(account.id)
    } else {
        account.rate
    }
    return account.copy(netInvestment = netInvestment, totalAsset = asset, rate = rate)
}

fun beforeComparedRecord(record: Record, comparedRecord: Record): Boolean {
    return record.date.isBefore(comparedRecord.date) || (record.date.isEqual(comparedRecord.date) && record.updateTime < comparedRecord.updateTime)
}

