package com.example.bookkeeping.util

import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record

fun updateAccountWhenInsertAmountRecord(
    account: Account, record: Record, latestRecord: Record?
): Account? = if (latestRecord == null) {
    account.copy(totalAsset = record.amount, netInvestment = record.amount)
} else if (latestRecord.date.isAfter(record.date)) {
    null
} else {
    with(account) {
        // TODO: 计算收益率
        copy(totalAsset = record.amount)
    }
}