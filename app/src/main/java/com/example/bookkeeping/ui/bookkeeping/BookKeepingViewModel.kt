package com.example.bookkeeping.ui.bookkeeping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.data.room.entity.Account
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookKeepingViewModel : ViewModel() {
//    val accountList: LiveData<List<Account>> = liveData {
//        Repository.getAccountList().collectLatest {
//            emit(it)
//        }
//    }

    val accountListLiveData = MutableLiveData<List<Account>>()
    val totalAssetLiveData = MutableLiveData<Double>()
    val netInvestmentLiveData = MutableLiveData<Double>()
    val profitLiveData = MutableLiveData<Double>()
    fun getAccountListAndSum() {
        viewModelScope.launch {
            Repository.getAccountListAndSum().collectLatest { (list, totalAsset, netInvestment) ->
                accountListLiveData.value = list
                totalAssetLiveData.value = totalAsset
                netInvestmentLiveData.value = netInvestment
                profitLiveData.value = totalAsset - netInvestment
            }
        }
    }

}