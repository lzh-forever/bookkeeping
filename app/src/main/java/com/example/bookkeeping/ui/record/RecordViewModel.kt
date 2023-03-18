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
        assetCompleteFlow.collect {
            emit(it)
        }
    }

    private val assetCompleteFlow = MutableStateFlow(false)

    fun onAssetTextChanged(s: CharSequence?) {
        val res = !s.isNullOrEmpty()
        if (assetCompleteFlow.value != res) {
            assetCompleteFlow.value = res
        }
    }

    fun addRecord(record:Record){
        viewModelScope.launch {
            Repository.addRecord(record)
        }
    }
}