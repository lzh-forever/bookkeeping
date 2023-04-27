package com.example.bookkeeping.data.room.entity

import android.os.Parcelable
import androidx.room.*
import com.example.bookkeeping.data.room.converter.RoomConverter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

import java.util.UUID

@Entity(tableName = "accounts")
@TypeConverters(RoomConverter::class)
@Parcelize
data class Account(
    @ColumnInfo(name = "account_name") @SerializedName("name") val name: String,
    @ColumnInfo(name = "total_asset") @SerializedName("total_asset") val totalAsset: Double = 0.0,
    @ColumnInfo(name = "net_investment") @SerializedName("net_investment") val netInvestment: Double = 0.0,
    @ColumnInfo(name = "rate_of_return") @SerializedName("rate_of_return") val rate: Double = 0.0,
    @ColumnInfo(name = "id") @SerializedName("id") val id: UUID = UUID.randomUUID(),
    @PrimaryKey(autoGenerate = true) @SerializedName("local_id") val databaseId: Int = 0,
    @ColumnInfo(name = "init_date") @SerializedName("init_date") val initDate: LocalDate? = null,
    @ColumnInfo(name = "init_asset") @SerializedName("init_asset") val initAsset: Double = 0.0,
    @ColumnInfo(name = "init_id") @SerializedName("init_id") val initId: UUID? = null,
) : Parcelable {
}

