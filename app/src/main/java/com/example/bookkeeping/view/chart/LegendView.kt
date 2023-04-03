package com.example.bookkeeping.view.chart

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Paint
import android.graphics.PathEffect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.LayoutLegendBinding
import com.example.bookkeeping.view.MyDatePicker

class LegendView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding: LayoutLegendBinding

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_legend, this)
        binding = LayoutLegendBinding.bind(view)
    }

    fun initLegend(text: String, color: Int, pathEffect: PathEffect? = null) {
        binding.legendTv.text = text
        binding.lineView.initPaint(color, pathEffect)
    }

}