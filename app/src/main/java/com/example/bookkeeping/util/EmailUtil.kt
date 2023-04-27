package com.example.bookkeeping.util

import android.util.Patterns
import android.widget.EditText
import java.util.regex.Pattern

private val emailPattern: Pattern by lazy { Patterns.EMAIL_ADDRESS }

fun checkEmail(view:EditText,block:(()->Unit)){
    if (!emailPattern.matcher(view.text.toString()).matches()){
        view.error = "请输入有效的邮箱地址"
        view.requestFocus()
    } else {
        block()
    }
}
