package com.example.bookkeeping.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bookkeeping.data.room.converter.LocalDateConverter
import java.time.LocalDate

@Entity(tableName = "records")
@TypeConverters(LocalDateConverter::class)
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "record_type") val type: RecordType,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "account_id") val accountId: Long
){

}
