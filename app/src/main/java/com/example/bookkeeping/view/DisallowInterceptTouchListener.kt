package com.example.bookkeeping.view

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener


class DisallowInterceptTouchListener : OnTouchListener {
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        Log.d("ChartView","listener: onTouch  action: ${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }
}