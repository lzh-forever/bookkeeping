package com.example.bookkeeping.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookkeeping.data.Repository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
//
//    fun changeText(str:String){
//        if (_text.value == str){
//            return
//        }
//        _text.value = str
//    }
    var textString = "This is home Fragment"

    fun test(){
        viewModelScope.launch {
            Repository.testLogin()
        }
    }

    fun backup(){
        viewModelScope.launch {
            Repository.backupDatabase()
//            Repository.restoreDatabase()
        }
    }

    fun restore(){
        viewModelScope.launch {
//            Repository.backupDatabase()
            Repository.restoreDatabase()
        }
    }
}