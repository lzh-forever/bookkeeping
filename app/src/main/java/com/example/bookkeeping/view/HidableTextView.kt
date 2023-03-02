package com.example.bookkeeping.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.bookkeeping.R
import com.example.bookkeeping.data.Repository
import kotlinx.coroutines.flow.collectLatest


class HidableTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {


    constructor(context: Context) : this(context, null)

    private var originalText: CharSequence
    private var mHide = false
    private val TAG = "HidableTextView"

    init {
        originalText = text
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, ViewTreeLifecycleOwner.get(this).toString() + " onAttachedToWindow")
        ViewTreeLifecycleOwner.get(this)?.lifecycleScope?.launchWhenCreated {
            Repository.hideFlow.collectLatest { hide ->
                mHide = hide
                text = if (hide) {
                    resources.getString(R.string.hide_text)
                } else {
                    originalText
                }
            }
        }
    }

    fun setHidableText(str:CharSequence){
        originalText = str
        text = if (mHide) {
            resources.getString(R.string.hide_text)
        } else {
            originalText
        }
    }

}