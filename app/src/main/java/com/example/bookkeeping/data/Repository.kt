package com.example.bookkeeping.data

import android.util.Log
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.network.*
import com.example.bookkeeping.data.room.AppDatabase
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.util.*
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object Repository {
    private val _hideFlow = MutableStateFlow(false)
    val hideFlow: StateFlow<Boolean>
        get() = _hideFlow

    var email: String = ""
    var username: String = ""
    var restoreDate = ""
    var backupDate = ""

    private val database by lazy { AppDatabase.getInstance(MyApplication.context) }
    private val recordMap = ConcurrentHashMap<UUID, List<Record>>()
    private val mutex = Mutex()
    private val gson by lazy {
        GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()
    }

    val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }
    var cookieJar: ClearableCookieJar =
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(MyApplication.context))

    // Create an OkHttpClient with the cookie manager
    val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    private val userService by lazy { retrofit.create(UserService::class.java) }
    private val backupService by lazy { retrofit.create(BackupService::class.java) }

    suspend fun login(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = JsonObject().apply {
                    addProperty("email", email)
                    addProperty("password", password)
                }
                val body = getJsonBody(json)
                val response = userService.login(body)
                if (response.code == 0) {
                    showShortToast("登录成功")
                    username = response.data
                    Repository.email = email
                    saveEmailAndUsername(email, username)
                    true
                } else {
                    showShortToast(response.msg)
                    false
                }
            } catch (e: Exception) {
                Log.d("retrofit", e.toString())
                showShortToast("登录失败")
                false
            }
        }
    }

    suspend fun register(
        email: String, username: String, password: String, captcha: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = JsonObject().apply {
                    addProperty("email", email)
                    addProperty("username", username)
                    addProperty("password", password)
                    addProperty("captcha", captcha)
                }
                val body = getJsonBody(json)
                val response = userService.register(body)
                if (response.code == 0) {
                    showShortToast("注册成功")
                    true
                } else {
                    showShortToast(response.msg)
                    false
                }
            } catch (e: Exception) {
                Log.d("retrofit", e.toString())
                showShortToast("注册失败")
                false
            }
        }
    }

    suspend fun sendCaptcha(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = JsonObject().apply {
                    addProperty("email", email)
                }
                val body = getJsonBody(json)
                val response = userService.sendCaptcha(body)
                if (response.code == 0) {
                    showShortToast("发送验证码成功")
                    true
                } else {
                    showShortToast(response.msg)
                    false
                }
            } catch (e: Exception) {
                Log.d("retrofit", e.toString())
                showShortToast("发送验证码失败")
                false
            }
        }
    }

    suspend fun backupDatabase(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val databaseBean = DatabaseBean().apply {
                    accounts = database.accountDao().getAllAccounts()
                    records = database.recordDao().getAllRecords()
                }
                val json = gson.toJsonTree(databaseBean).asJsonObject
                Log.d("retrofit", json.toString())
                val body = getJsonBody(json)
                val response = backupService.backup(body)
                if (response.code == 0) {
                    showShortToast("备份成功")
                    backupDate = LocalDate.now().toString()
                    saveBackupDate(backupDate)
                    true
                } else {
                    showShortToast(response.msg)
                    false
                }
            } catch (e: Exception) {
                Log.d("retrofit", e.toString())
                showShortToast("备份失败")

                false
            }
        }
    }

    suspend fun restoreDatabase(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = backupService.getBackup()
                val accounts = response.data.accounts
                val records = response.data.records
                if (response.code == 0) {
                    accounts?.let {
                        database.accountDao().deleteAll()
                        database.accountDao().insertAll(it)
                    }
                    records?.let {
                        database.recordDao().deleteAll()
                        database.recordDao().insertAll(records)
                    }
                    recordMap.clear()
                    restoreDate = LocalDate.now().toString()
                    saveRestoreDate(restoreDate)
                    showShortToast("同步成功")
                    true
                } else {
                    showShortToast(response.msg)
                    false
                }
            } catch (e: Exception) {
                Log.d("retrofit", e.toString())
                showShortToast("同步失败")
                false
            }
        }
    }


    suspend fun testLogin() {
        withContext(Dispatchers.IO) {
            login("578535149@qq.com", "123456")
//            backupDatabase()
        }
    }

    private fun getJsonBody(json: JsonObject) =
        json.toString().toRequestBody("application/json".toMediaTypeOrNull())

    fun changeHideFlowValue() {
        _hideFlow.value = !_hideFlow.value
    }

    fun getAccountListAndSum(): Flow<Triple<List<Account>, Double, Double>> =
        database.accountDao().getAllAccountsFlow().flowOn(Dispatchers.IO).map { list ->
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