package com.example.bookkeeping.util

import android.graphics.Paint

fun Paint.getTextHeight(): Float {
    return fontMetrics.bottom - fontMetrics.top
}

fun Paint.getTextWidth(text: String): Float {
    return measureText(text)
}

fun Paint.getBaselineHeight(): Float {
    return -fontMetrics.top
}

fun Paint.getBaselineYForCenter(centerY: Float): Float {
    return centerY - fontMetrics.top / 2 - fontMetrics.bottom / 2
}