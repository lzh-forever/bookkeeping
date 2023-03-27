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
        initDate = record.date, initAsset = record.amount
    )
} else if (latestAmountRecord.date.isAfter(record.date)) {
    //晚于上次总额记录，不用更新总资产
    //更新初始资产时需要更改净投入
    var resAccount: Account? = null
    if (record.date.isBefore(account.initDate)) {
        with(account) {
            val rate = getXirrRate(id)
            resAccount = copy(
                netInvestment = netInvestment - initAsset + record.amount,
                initDate = record.date, initAsset = record.amount, rate = rate
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
    account: Account,
    record: Record,
    latestAmountRecord: Record
): Account {
    val sign = if (record.type == RecordType.TRANSFER_IN) 1 else -1
    val amount = sign * record.amount
    if (latestAmountRecord.date.isAfter(record.date)) {
        //早于上次总额记录，只更新净投入
        val rate = getXirrRate(account.id)
        return account.copy(netInvestment = account.netInvestment + amount, rate = rate)
    } else {
        //最新记录，更新净投入和资产，
        return account.copy(
            netInvestment = account.netInvestment + amount,
            totalAsset = account.totalAsset + amount
        )
    }

}

