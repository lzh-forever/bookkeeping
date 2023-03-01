package com.example.bookkeeping

import kotlinx.coroutines.flow.MutableStateFlow

object Repository {
    val hideFlow = MutableStateFlow(false)
}