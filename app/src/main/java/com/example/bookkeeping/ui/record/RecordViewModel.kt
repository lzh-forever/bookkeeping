package com.example.bookkeeping.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Record
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecordViewModel: ViewModel() {
    val completed: LiveData<Boolean> = liveData {
        assertsCompleteFlow.collect {
            emit(it)
        }
    }

    private val assertsCompleteFlow = MutableStateFlow(false)

    fun onAssertsTextChanged(s: CharSequence?) {
        val res = !s.isNullOrEmpty()
        if (assertsCompleteFlow.value != res) {
            assertsCompleteFlow.value = res
        }
    }

    fun addRecord(record:Record){
        viewModelScope.launch {
            Repository.addRecord(record)
        }
    }
}