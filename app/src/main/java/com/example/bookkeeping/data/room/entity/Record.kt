package com.example.bookkeeping.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bookkeeping.data.room.converter.RoomConverter
import java.time.LocalDate
import java.util.*

@Entity(tableName = "records")
@TypeConverters(RoomConverter::class)
data class Record(
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "record_type") val type: RecordType,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "account_id") val accountId: UUID,
    @PrimaryKey val id: UUID = UUID.randomUUID()
) {

}
