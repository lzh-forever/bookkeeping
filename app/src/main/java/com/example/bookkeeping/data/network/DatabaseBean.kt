package com.example.bookkeeping.data.network

import com.example.bookkeeping.data.room.entity.Account
import com.example.bookkeeping.data.room.entity.Record
import com.google.gson.annotations.SerializedName

class DatabaseBean {
    @SerializedName("accounts")
    var accounts: List<Account>? = null

    @SerializedName("records")
    var records: List<Record>? = null
}