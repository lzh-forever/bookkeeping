package com.example.bookkeeping.data.room.entity

import android.os.Parcelable
import androidx.room.*
import com.example.bookkeeping.data.room.converter.RoomConverter
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

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
    @PrimaryKey(autoGenerate = true) val databaseId: Int = 0,
    @ColumnInfo(name = "init_date") val initDate: LocalDate? = null,
    @ColumnInfo(name = "init_asset") val initAsset:Double = 0.0
) : Parcelable {
}

