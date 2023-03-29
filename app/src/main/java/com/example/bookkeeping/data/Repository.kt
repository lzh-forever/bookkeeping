package com.example.bookkeeping.data

import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.room.AppDatabase
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object Repository {
    private val _hideFlow = MutableStateFlow(false)
    val hideFlow: StateFlow<Boolean>
        get() = _hideFlow
    private val database by lazy { AppDatabase.getInstance(MyApplication.context) }
    private val recordMap = ConcurrentHashMap<UUID, List<Record>>()
    private val mutex = Mutex()

    fun changeHideFlowValue(){
        _hideFlow.value = !_hideFlow.value
    }

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

    suspend fun getGroupedRecordList(accountId: UUID): List<List<Record>> {
        return getRecordList(accountId).reversed().groupBy { it.date }.map { it.value }
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

    suspend fun updateRecord(originalRecord: Record, record: Record, account: Account) {
        withContext(Dispatchers.IO) {
            updateRecord(originalRecord, record)
            when (record.type) {
                RecordType.CURRENT_AMOUNT -> {
                    val updateAccount =
                        updateAccountWhenUpdateAmountRecord(record, account)
                    database.accountDao().update(updateAccount)
                }
                else -> {
                    val latestAmountRecord = database.recordDao().getLatestRecordByAccountAndType(
                        account.id, RecordType.CURRENT_AMOUNT
                    )
                    updateAccountWhenUpdateTransferRecord(
                        originalRecord, record, account, latestAmountRecord!!
                    )?.let {
                        database.accountDao().update(it)
                    }
                }
            }
        }
    }

    suspend fun deleteRecord(record: Record, account: Account) {
        withContext(Dispatchers.IO) {
            removeRecord(record)
            val latestAmountRecord = database.recordDao().getLatestRecordByAccountAndType(
                account.id, RecordType.CURRENT_AMOUNT
            )
            when (record.type) {
                RecordType.CURRENT_AMOUNT -> {
                    updateAccountWhenDeleteAmountRecord(
                        record,
                        account,
                        latestAmountRecord!!
                    )?.let {
                        database.accountDao().update(it)
                    }
                }
                else -> {
                    updateAccountWhenDeleteTransferRecord(
                        record,
                        account,
                        latestAmountRecord!!
                    ).let {
                        database.accountDao().update(it)
                    }
                }
            }
        }
    }

    private suspend fun insertRecord(record: Record) {
        withContext(Dispatchers.Default) {
            addAndUpdateCache(record)
        }
        database.recordDao().insert(record)
    }

    private suspend fun updateRecord(originalRecord: Record, record: Record) {
        withContext(Dispatchers.Default) {
            removeAndUpdateCache(originalRecord)
            addAndUpdateCache(record)
        }
        database.recordDao().update(record)
    }

    private suspend fun removeRecord(record: Record) {
        withContext(Dispatchers.Default) {
            removeAndUpdateCache(record)
        }
        database.recordDao().delete(record)
    }

    private suspend fun addAndUpdateCache(record: Record) {
        recordMap[record.accountId]?.let {
            mutex.withLock {
                val list = it.toMutableList()
                var index = 0
                for (r in list) {
                    if (r.date < record.date) {
                        index++
                        continue
                    }
                    break
                }
                for (i in index..list.lastIndex) {
                    val r = list[i]
                    if (r.date == record.date && r.updateTime <= record.updateTime) {
                        index++
                        continue
                    }
                    break
                }
                list.add(index, record)
                recordMap[record.accountId] = list
            }
        }
    }

    private suspend fun removeAndUpdateCache(record: Record) {
        recordMap[record.accountId]?.let {
            mutex.withLock {
                val list = it.toMutableList()
                list.remove(record)
                recordMap[record.accountId] = list
            }
        }
    }

    fun getRecordFlowByAccountId(
        accountId: UUID, limit: Int = 3
    ): Flow<List<List<Record>>> {
        if (limit < 0) {
            return emptyFlow()
        }
        return database.recordDao().getRecordsByAccountIdWithLimit(accountId, limit)
            .flowOn(Dispatchers.IO).map { records ->
                records.groupBy { it.date }.map { it.value }
            }.flowOn(Dispatchers.Default)
    }


}