package com.example.bookkeeping.data.room.dao

import androidx.room.*
import com.example.bookkeeping.data.room.entity.Account
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<Account>)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: UUID): Account

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountFlowById(id: UUID): Flow<Account>

    @Query("SELECT * FROM accounts")
    fun getAllAccountsFlow(): Flow<List<Account>>

    @Query("SELECT * FROM accounts")
    suspend fun getAllAccounts(): List<Account>
}