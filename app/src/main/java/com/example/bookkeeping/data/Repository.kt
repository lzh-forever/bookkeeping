package com.example.bookkeeping.data

import android.util.Log
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.room.AppDatabase
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.util.updateAccountWhenInsertAmountRecord
import com.example.bookkeeping.util.updateAccountWhenInsertTransferRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object Repository {
    val hideFlow = MutableStateFlow(false)
    private val database by lazy { AppDatabase.getInstance(MyApplication.context) }
    private val recordMap = ConcurrentHashMap<UUID, List<Record>>()
    private val mutex = Mutex()

    fun getAccountListAndSum(): Flow<Triple<List<Account>, Double, Double>> =
        database.accountDao().getAllAccounts().flowOn(Dispatchers.IO).map { list ->
            Triple(list, list.sumOf { it.totalAsset }, list.sumOf { it.netInvestment })
        }.flowOn(Dispatchers.Default)

    fun getAccountFlowById(id: UUID): Flow<Account> =
        database.accountDao().getAccountFlowById(id).flowOn(Dispatchers.IO)


    suspend fun createAccount(accountName: String) {
        withContext(Dispatchers.IO) {
            database.accountDao().insert(Account(name = accountName))
        }
    }


    suspend fun updateAccount(account: Account) {
        withContext(Dispatchers.IO) {
            database.accountDao().update(account)
        }
    }


    suspend fun getRecordList(accountId: UUID): List<Record> {
        // 如果缓存中存在记录列表，则直接返回缓存的列表
        recordMap[accountId]?.let {
            return it
        }
        // 加锁以避免竞态条件
        mutex.withLock {
            //double check
            recordMap[accountId]?.let {
                return it
            }
            // 如果缓存中不存在记录列表，则从数据库中查询记录列表
            val databaseRecords = database.recordDao().getRecordsByAccountId(accountId)
            // 将查询到的记录列表存储到缓存中
            recordMap[accountId] = databaseRecords
            return databaseRecords
        }
    }

    fun cacheRecordList(accountId: UUID, list: List<Record>) {
        recordMap[accountId] = list
    }

    private suspend fun insertRecord(record: Record, cache: Boolean = true) {
        if (cache && recordMap.containsKey(record.accountId)) {
            mutex.withLock {
                val list = recordMap[record.accountId]!!.toMutableList()
                var index = 0
                for (r in list) {
                    if (r.date <= record.date && r.createTime <= record.createTime) {
                        index++
                        continue
                    }
                    break
                }
                list.add(index, record)
                recordMap[record.accountId] = list
            }
        }
        database.recordDao().insert(record)
    }

    suspend fun addRecord(record: Record, account: Account) {
        withContext(Dispatchers.IO) {
            val latestAmountRecord = database.recordDao().getLatestRecordByAccountAndType(
                account.id, RecordType.CURRENT_AMOUNT
            )
            insertRecord(record)
            when (record.type) {
                RecordType.CURRENT_AMOUNT -> {
                    updateAccountWhenInsertAmountRecord(
                        account, record, latestAmountRecord
                    )?.let {
                        database.accountDao().update(it)
                    }
                }
                else -> {
                    with(
                        updateAccountWhenInsertTransferRecord(
                            account, record, latestAmountRecord!!
                        )
                    ) {
                        database.accountDao().update(this)
                    }
                }
            }
        }
    }

    fun getRecordFlowByAccountId(
        accountId: UUID, limit: Int = 0
    ): Flow<List<List<Record>>> {
        if (limit < 0) {
            return emptyFlow()
        }
        return if (limit == 0) {
            database.recordDao().getRecordsReverseFlowByAccountId(accountId)
        } else {
            database.recordDao().getRecordsByAccountIdWithLimit(accountId, limit)
        }.flowOn(Dispatchers.IO).map { records ->
            records.groupBy { it.date }.map { it.value }
        }.flowOn(Dispatchers.Default)
    }


}