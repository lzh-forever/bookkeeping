package com.example.bookkeeping.data

import android.util.Log
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.room.AppDatabase
import com.example.bookkeeping.data.room.entity.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

object Repository {
    val hideFlow = MutableStateFlow(false)
    val database by lazy { AppDatabase.getInstance(MyApplication.context) }

    fun getAccountList(): Flow<List<Account>> =
        database.accountDao().getAllAccounts().flowOn(Dispatchers.IO)

//    fun getAccountList(): Flow<List<Account>> = flow {
//        val list = ArrayList<Account>()
//        list.apply {
//            add(Account(1,"账户1",12000.0,10000.0,2000.0))
//        }
//        Log.d("flow","before emit")
//        emit(list)
//    }.flowOn(Dispatchers.IO)


}