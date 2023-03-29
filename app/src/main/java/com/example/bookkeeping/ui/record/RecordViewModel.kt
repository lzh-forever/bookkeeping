package com.example.bookkeeping.ui.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.R
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.example.bookkeeping.data.room.entity.RecordType
import com.example.bookkeeping.util.showShortToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecordViewModel : ViewModel() {

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

    fun addRecord(record: Record, account: Account, block: (() -> Unit)? = null) {
        if (record.type.isTransferType() && record.date.isBefore(account.initDate)) {
            showShortToast(
                MyApplication.context.resources.getString(
                    R.string.err_msg_transfer_record_date, account.initDate.toString()
                )
            )
            return
        }
        viewModelScope.launch {
            Repository.addRecord(record, account)
            block?.invoke()
        }
    }

    fun updateRecord(
        originalRecord: Record, record: Record, account: Account, block: (() -> Unit)? = null
    ) {
        if (record.type.isTransferType() && record.date.isBefore(account.initDate)) {
            showShortToast(
                MyApplication.context.resources.getString(
                    R.string.err_msg_transfer_record_date, account.initDate.toString()
                )
            )
            return
        }
        if (record.id == account.initId && !record.date.isEqual(account.initDate)) {
            showShortToast(
                MyApplication.context.resources.getString(
                    R.string.err_msg_update_init_record_date, account.initDate.toString()
                )
            )
            return
        }
        if (originalRecord == record) {
            block?.invoke()
            return
        }
        viewModelScope.launch {
            Repository.updateRecord(originalRecord, record, account)
            block?.invoke()
        }
    }

    fun deleteRecord(
        record: Record, account: Account, block: (() -> Unit)? = null
    ) {
        if (record.id == account.initId) {
            showShortToast(MyApplication.context.resources.getString(R.string.err_msg_delete_init_record))
            return
        }

        viewModelScope.launch {
            Repository.deleteRecord(record, account)
            block?.invoke()
        }
    }

    fun setRecordType(recordType: RecordType) {
        recordTypeLiveData.value = recordType
    }

}