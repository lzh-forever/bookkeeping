package com.example.bookkeeping.util

fun getFormattedDouble(double: Double) = String.format("%.2f", double)

fun getFormattedRate(double: Double) = String.format("%.2f%%", double * 100)