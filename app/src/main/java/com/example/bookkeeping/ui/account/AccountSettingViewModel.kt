package com.example.bookkeeping.ui.account

import android.util.Log
import androidx.lifecycle.*
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AccountSettingViewModel : ViewModel() {

    val completed: LiveData<Boolean> = liveData {
        textCompleteFlow.collect {
            emit(it)
        }
    }

    private val textCompleteFlow = MutableStateFlow(false)

    fun onNameTextChanged(s: CharSequence?) {
        val res = !s.isNullOrEmpty()
        if (textCompleteFlow.value != res) {
            textCompleteFlow.value = res
        }
    }

    fun addAccount(accountName:String) {
        viewModelScope.launch {
            Repository.createAccount(accountName)
        }
    }

    fun updateAccount(accountName: String,account:Account){
        viewModelScope.launch {
            Repository.updateAccount(account.copy(name = accountName))
        }
    }
}