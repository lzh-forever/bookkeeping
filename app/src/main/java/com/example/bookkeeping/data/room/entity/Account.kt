package com.example.bookkeeping.data.room.entity

import android.os.Parcelable
import androidx.room.*
import com.example.bookkeeping.data.room.converter.RoomConverter
import kotlinx.parcelize.Parcelize

import java.util.UUID

@Entity(tableName = "accounts")
@TypeConverters(RoomConverter::class)
@Parcelize
data class Account(
    @ColumnInfo(name = "account_name") val name: String,
    @ColumnInfo(name = "total_asset") val totalAsset: Double = 0.0,
    @ColumnInfo(name = "net_investment") val netInvestment: Double = 0.0,
    @ColumnInfo(name = "rate_of_return") val rate: Double = 0.0,
    @ColumnInfo(name = "id") val id: UUID = UUID.randomUUID(),
    @PrimaryKey(autoGenerate = true) val databaseId:Int = 0
):Parcelable {
    fun updateFromRecords(records: List<Record>) = copy(
        name = name,
        totalAsset = records.sumOf { it.amount },
        netInvestment = records.filter { it.type.isTransferType() }.sumOf { it.amount },
    )




}

