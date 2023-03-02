package com.example.bookkeeping.data

import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.room.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow

object Repository {
    val hideFlow = MutableStateFlow(false)
    val database by lazy { AppDatabase.getInstance(MyApplication.context) }
}