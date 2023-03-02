package com.example.bookkeeping.data.room.converter

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): LocalDate? {
        return if (timestamp == null) null else LocalDate.ofEpochDay(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}