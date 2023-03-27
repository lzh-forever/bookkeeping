package com.example.bookkeeping.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class AccountDetailViewModel : ViewModel() {

    private val limit = 3

    fun getAccountFlowById(id: UUID) = liveData<Account> {
        Repository.getAccountFlowById(id).collectLatest {
            emit(it)
        }
    }

    fun getRecordFlowById(id: UUID) = liveData<List<List<Record>>> {
        Repository.getRecordFlowByAccountId(id,limit).collectLatest {
            emit(it)
        }
    }

}