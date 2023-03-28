package com.example.bookkeeping.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.bookkeeping.R


import java.time.LocalDate

@SuppressLint("SetTextI18n")
class MyDatePicker(context: Context, attrs: AttributeSet) : CardView(context, attrs),
    View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private val text: TextView
    private val datePickerDialog: DatePickerDialog
    var localDate: LocalDate = LocalDate.now()
        private set

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_datepicker, this)
        text = findViewById(R.id.date_tv)
        text.text = localDate.toString() + TODAY
        datePickerDialog =
            DatePickerDialog(context).apply { setOnDateSetListener(this@MyDatePicker) }
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        datePickerDialog.show()
    }

    fun updateDate(date: LocalDate) {
        with(date) {
            datePickerDialog.updateDate(year, monthValue - 1, dayOfMonth)
            localDate = date
            setDateText()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        localDate = LocalDate.of(year, month + 1, dayOfMonth)
        setDateText()
    }

    private fun setDateText() {
        val extra = if (LocalDate.now().isEqual(localDate)) {
            TODAY
        } else {
            ""
        }
        text.text = localDate.toString() + extra
    }

    companion object {
        private const val TODAY = "（今天）"
    }

}