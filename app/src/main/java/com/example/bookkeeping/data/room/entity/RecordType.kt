package com.example.bookkeeping.data.room.entity

enum class RecordType {
    TRANSFER_IN,
    TRANSFER_OUT,
    CURRENT_AMOUNT;

    fun isTransferType() = this == TRANSFER_IN || this == TRANSFER_OUT
}
