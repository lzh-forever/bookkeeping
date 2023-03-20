package com.example.bookkeeping.ui.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecordViewModel: ViewModel() {

    val recordTypeLiveData = MutableLiveData<RecordType>()

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

    fun addRecord(record:Record,account: Account){
        viewModelScope.launch {
            Repository.addRecord(record,account)
        }
    }

    fun setRecordType(recordType: RecordType){
        recordTypeLiveData.value = recordType
    }

}