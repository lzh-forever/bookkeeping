package com.example.bookkeeping.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookkeeping.data.room.dao.AccountDao
import com.example.bookkeeping.data.room.dao.RecordDao
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record

@Database(version = 1, entities = [Account::class, Record::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun recordDao(): RecordDao


    companion object {
        private const val DATABASE_NAME = "app_database"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build().apply { instance = this }
            }
        }
    }

}