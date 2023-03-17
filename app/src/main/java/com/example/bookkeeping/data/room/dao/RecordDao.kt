package com.example.bookkeeping.data.room.dao

import androidx.room.*
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: UUID): Record

    @Query("SELECT * FROM records WHERE account_id = :accountId ORDER BY date DESC")
    fun getRecordsByAccount(accountId: UUID): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE account_id = :accountId AND record_type = :type ORDER BY date DESC LIMIT 1")
    suspend fun getLatestRecordByAccountAndType(accountId: UUID, type: RecordType): Record?

}