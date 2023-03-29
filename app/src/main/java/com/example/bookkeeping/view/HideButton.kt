package com.example.bookkeeping.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.bookkeeping.R
import com.example.bookkeeping.data.Repository
import kotlinx.coroutines.flow.collectLatest


class HideButton(context: Context, attrs: AttributeSet?) : AppCompatImageButton(context, attrs),OnClickListener {

    init {
        setOnClickListener(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ViewTreeLifecycleOwner.get(this)?.lifecycleScope?.launchWhenCreated {
            Repository.hideFlow.collectLatest { hide ->
                background = if (hide) {
                    ContextCompat.getDrawable(context,R.drawable.ic_eye_closed)
                } else {
                    ContextCompat.getDrawable(context,R.drawable.ic_eye)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        Repository.changeHideFlowValue()
    }
}