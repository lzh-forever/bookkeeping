package com.example.bookkeeping.util

import android.content.Context
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.data.Repository

private val sp by lazy { MyApplication.context.getSharedPreferences("user", Context.MODE_PRIVATE) }

fun loadUserInfo() {
    sp.getString("email", null)?.let {
        Repository.email = it
    }
    sp.getString("username", null)?.let {
        Repository.username = it
    }
    sp.getString("restoreDate", null)?.let {
        Repository.restoreDate = it
    }
    sp.getString("backupDate", null)?.let {
        Repository.backupDate = it
    }
}

fun saveEmailAndUsername(email: String, username: String) {
    sp.edit().apply() {
        putString("email", email)
        putString("username", username)
        apply()
    }
}

fun saveRestoreDate(date: String) {
    sp.edit().apply {
        putString("restoreDate", date)
        apply()
    }
}

fun saveBackupDate(date: String) {
    sp.edit().apply {
        putString("backupDate", date)
        apply()
    }
}

fun clearUserInfo() {
    sp.edit().apply {
        clear()
        apply()
    }
}
