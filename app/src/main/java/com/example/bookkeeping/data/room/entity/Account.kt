package com.example.bookkeeping.data.room.entity

import androidx.room.*
import com.example.bookkeeping.data.room.converter.RoomConverter
import java.util.UUID

@Entity(tableName = "accounts")
@TypeConverters(RoomConverter::class)
data class Account(
    @ColumnInfo(name = "account_name") val name: String,
    @ColumnInfo(name = "total_asset") val totalAsset: Double = 0.0,
    @ColumnInfo(name = "net_investment") val netInvestment: Double = 0.0,
    @ColumnInfo(name = "rate_of_return") val rate: Double = 0.0,
    @PrimaryKey val id: UUID = UUID.randomUUID()
) {
    fun updateFromRecords(records: List<Record>) = copy(
        name = name,
        totalAsset = records.sumOf { it.amount },
        netInvestment = records.filter { it.type.isTransferType() }.sumOf { it.amount },
    )




}

