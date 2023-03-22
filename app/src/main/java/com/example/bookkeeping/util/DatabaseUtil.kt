package com.example.bookkeeping.util

import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType

fun updateAccountWhenInsertAmountRecord(
    account: Account, record: Record, latestAmountRecord: Record?
): Account? = if (latestAmountRecord == null) {
    //初次记账，更新净投入和资产
    account.copy(totalAsset = record.amount, netInvestment = record.amount)
} else if (latestAmountRecord.date.isAfter(record.date)) {
    //晚于上次总额记录，不用更新
    // TODO: 初始资产
    null
} else {
    //更新总额
    with(account) {
        // TODO: 计算收益率
        copy(totalAsset = record.amount)
    }
}

fun updateAccountWhenInsertTransferRecord(
    account: Account,
    record: Record,
    latestAmountRecord: Record
): Account {
    val sign = if (record.type == RecordType.TRANSFER_IN) 1 else -1
    val amount = sign * record.amount
    if (latestAmountRecord.date.isAfter(record.date)) {
        //晚于上次总额记录，只更新净投入
        // todo 计算收益率，需要所有记录
        return account.copy(netInvestment = account.netInvestment + sign * amount)
    } else {
        //最新记录，更新净投入和资产，
        return account.copy(
            netInvestment = account.netInvestment + amount,
            totalAsset = account.totalAsset + amount
        )
    }

}

