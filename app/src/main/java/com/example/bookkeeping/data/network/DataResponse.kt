package com.example.bookkeeping.data.network

class DataResponse<T>(val data: T, code: Int, msg: String) : Response(code, msg)