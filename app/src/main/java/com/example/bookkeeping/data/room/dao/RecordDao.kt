package com.example.bookkeeping.data.room.dao

import androidx.room.*
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: Long): Record

    @Query("SELECT * FROM records WHERE account_id = :accountId")
    fun getRecordsByAccount(accountId: Long): Flow<List<Record>>

}