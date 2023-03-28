package com.example.bookkeeping.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Record
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class RecordListViewModel : ViewModel() {

    private val _recordListLiveData: MutableLiveData<List<List<Record>>> = MutableLiveData()
    val recordListLiveData:LiveData<List<List<Record>>>
        get() = _recordListLiveData

    fun getRecordById(id: UUID) {
        viewModelScope.launch {
            val res = Repository.getGroupedRecordList(id)
            _recordListLiveData.value = res
        }
    }

}