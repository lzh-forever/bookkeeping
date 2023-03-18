package com.example.bookkeeping.data

import android.util.Log
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.room.AppDatabase
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.util.updateAccountWhenInsertAmountRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.UUID

object Repository {
    val hideFlow = MutableStateFlow(false)
    private val database by lazy { AppDatabase.getInstance(MyApplication.context) }

    fun getAccountList(): Flow<List<Account>> =
        database.accountDao().getAllAccounts().flowOn(Dispatchers.IO)

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

    suspend fun updateAccount(account: Account){
        withContext(Dispatchers.IO){
            database.accountDao().update(account)
        }
    }


    suspend fun addRecord(record: Record, account: Account? = null) {
        withContext(Dispatchers.IO) {
            val updateAccount = account ?: database.accountDao().getAccountById(record.accountId)
            Log.d("database", updateAccount.toString())
            val latestRecord = database.recordDao()
                .getLatestRecordByAccountAndType(updateAccount.id, RecordType.CURRENT_AMOUNT)
            Log.d("database", latestRecord.toString())
            database.recordDao().insert(record)
            updateAccountWhenInsertAmountRecord(updateAccount, record, latestRecord)?.let {
                database.accountDao().update(it)
            }

        }
    }


}