package com.example.bookkeeping.data.room.entity

enum class RecordType {
    TRANSFER_IN,
    TRANSFER_OUT,
    CURRENT_AMOUNT;

    fun isTransferType() = this == TRANSFER_IN || this == TRANSFER_OUT

    override fun toString(): String = when (this) {
        TRANSFER_IN -> "转入"
        TRANSFER_OUT -> "转出"
        CURRENT_AMOUNT -> "更新资产"
    }

}
