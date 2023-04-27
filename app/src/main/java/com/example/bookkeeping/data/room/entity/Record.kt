package com.example.bookkeeping.data.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bookkeeping.data.room.converter.RoomConverter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

import java.time.LocalDate
import java.util.*

@Entity(tableName = "records")
@TypeConverters(RoomConverter::class)
@Parcelize
data class Record(
    @ColumnInfo(name = "date") @SerializedName("date") val date: LocalDate,
    @ColumnInfo(name = "record_type") @SerializedName("record_type") val type: RecordType,
    @ColumnInfo(name = "amount") @SerializedName("amount") val amount: Double,
    @ColumnInfo(name = "account_id") @SerializedName("account_id") val accountId: UUID,
    @ColumnInfo(name = "id") @SerializedName("record_id") val id: UUID = UUID.randomUUID(),
    @PrimaryKey(autoGenerate = true) @SerializedName("local_id") val databaseId: Int = 0,
    @ColumnInfo(name = "update_time") @SerializedName("update_time") var updateTime: Long = System.currentTimeMillis()
) : Parcelable {
}
