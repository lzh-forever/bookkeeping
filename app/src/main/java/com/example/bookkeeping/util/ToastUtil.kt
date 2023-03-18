package com.example.bookkeeping.util

import android.util.Log
import android.widget.Toast
import com.example.bookkeeping.MyApplication
import com.example.bookkeeping.R

fun showShortToast(s: CharSequence) {
    Toast.makeText(MyApplication.context, s, Toast.LENGTH_SHORT).show()
}

fun showArgsExceptionToast(tag:String) {
    Toast.makeText(
        MyApplication.context,
        MyApplication.context.resources.getText(R.string.args_exception),
        Toast.LENGTH_SHORT
    ).show()
    Log.d(tag,"args is not initialized")

}