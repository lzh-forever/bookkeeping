package com.example.bookkeeping.data.room.entity

import androidx.room.*

@Entity(tableName = "accounts")
data class Account(
    @ColumnInfo(name = "account_name") val name: String,
    @ColumnInfo(name = "total_asset") val totalAsset: Double = 0.0,
    @ColumnInfo(name = "net_investment") val netInvestment: Double = 0.0,
    @ColumnInfo(name = "profit") val profit: Double = 0.0,
    @ColumnInfo(name = "rate_of_return") val rate:Double = 0.0,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    fun updateFromRecords(records: List<Record>) = copy(
        id = id,
        name = name,
        totalAsset = records.sumOf { it.amount },
        netInvestment = records.filter { it.type.isTransferType() }.sumOf { it.amount },
        profit = totalAsset - netInvestment
    )


}

