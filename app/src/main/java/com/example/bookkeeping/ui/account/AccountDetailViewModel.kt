package com.example.bookkeeping.ui.account

import androidx.lifecycle.*
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.util.getModifiedDietzRate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class AccountDetailViewModel : ViewModel() {

    private val limit = 3

    private val _recordListLiveData: MutableLiveData<List<Record>> = MutableLiveData()
    val recordListLiveData: LiveData<List<Record>>
        get() = _recordListLiveData

    private val _rateLiveData: MutableLiveData<List<Pair<LocalDate, Double>>> = MutableLiveData()
    val rateLiveData: LiveData<List<Pair<LocalDate, Double>>>
        get() = _rateLiveData

    fun getRecordById(id: UUID) {
        viewModelScope.launch {
            val res = Repository.getRecordList(id)
            _recordListLiveData.value = res
            calculateRate(res)
        }
    }

    fun calculateRate(list: List<Record>) {
        viewModelScope.launch {
            val res = getModifiedDietzRate(list)
            _rateLiveData.value = res
        }
    }

    fun getAccountFlowById(id: UUID) = liveData<Account> {
        Repository.getAccountFlowById(id).collectLatest {
            emit(it)
        }
    }

    fun getRecordFlowById(id: UUID) = liveData<List<List<Record>>> {
        Repository.getRecordFlowByAccountId(id, limit).collectLatest {
            emit(it)
        }
    }

}