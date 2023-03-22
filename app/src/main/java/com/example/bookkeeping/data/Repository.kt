package com.example.bookkeeping.data

import android.util.Log
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.room.AppDatabase
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.util.updateAccountWhenInsertAmountRecord
import com.example.bookkeeping.util.updateAccountWhenInsertTransferRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.UUID

object Repository {
    val hideFlow = MutableStateFlow(false)
    private val database by lazy { AppDatabase.getInstance(MyApplication.context) }

    fun getAccountList(): Flow<List<Account>> =
        database.accountDao().getAllAccounts().flowOn(Dispatchers.IO)

    fun getAccountListAndSum(): Flow<Triple<List<Account>, Double, Double>> =
        database.accountDao().getAllAccounts().flowOn(Dispatchers.IO).map { list ->
            Triple(list, list.sumOf { it.totalAsset }, list.sumOf { it.netInvestment })
        }.flowOn(Dispatchers.Default)

    fun getAccountFlowById(id: UUID): Flow<Account> =
        database.accountDao().getAccountFlowById(id).flowOn(Dispatchers.IO)

//    fun getAccountList(): Flow<List<Account>> = flow {
//        val list = ArrayList<Account>()
//        list.apply {
//            add(Account("账户1",12000.0,10000.0,2000.0))
//        }
//        Log.d("flow","before emit")
//        emit(list)
//    }.flowOn(Dispatchers.IO)

    suspend fun createAccount(accountName: String) {
        withContext(Dispatchers.IO) {
            database.accountDao().insert(Account(name = accountName))
        }
    }

    suspend fun getAccountById(id: UUID) =
        withContext(Dispatchers.IO) {
            database.accountDao().getAccountById(id)
        }

    suspend fun updateAccount(account: Account) {
        withContext(Dispatchers.IO) {
            database.accountDao().update(account)
        }
    }


    suspend fun addRecord(record: Record, account: Account? = null) {
        withContext(Dispatchers.IO) {
            val updateAccount = account ?: database.accountDao().getAccountById(record.accountId)
            Log.d("database", updateAccount.toString())
            val latestAmountRecord = database.recordDao()
                .getLatestRecordByAccountAndType(
                    updateAccount.id,
                    RecordType.CURRENT_AMOUNT
                )
            when (record.type) {
                RecordType.CURRENT_AMOUNT -> {

                    Log.d("database", latestAmountRecord.toString())
                    updateAccountWhenInsertAmountRecord(
                        updateAccount,
                        record,
                        latestAmountRecord
                    )?.let {
                        database.accountDao().update(it)
                    }
                }
                else -> {
                    with(
                        updateAccountWhenInsertTransferRecord(
                            updateAccount,
                            record,
                            latestAmountRecord!!
                        )
                    ) {
                        database.accountDao().update(this)
                    }
                }
            }
            database.recordDao().insert(record)
        }
    }

    fun getRecordFlowByAccountId(
        accountId: UUID,
        limit: Int = 0
    ): Flow<List<List<Record>>> {
        if (limit < 0) {
            return emptyFlow()
        }
        return if (limit == 0) {
            database.recordDao().getRecordsByAccountId(accountId)
        } else {
            database.recordDao().getRecordsByAccountIdWithLimit(accountId, limit)
        }.flowOn(Dispatchers.IO).map { records ->
            records.groupBy { it.date }.map { it.value }
        }.flowOn(Dispatchers.Default)
    }


}