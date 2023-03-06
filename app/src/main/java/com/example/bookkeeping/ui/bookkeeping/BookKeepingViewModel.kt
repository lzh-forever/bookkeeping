package com.example.bookkeeping.ui.bookkeeping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import kotlinx.coroutines.flow.collectLatest

class BookKeepingViewModel : ViewModel() {
    val accountList:LiveData<List<Account>> = liveData {
        Repository.getAccountList().collectLatest {
            emit(it)
        }
    }

}