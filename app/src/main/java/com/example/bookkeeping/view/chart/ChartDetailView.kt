package com.example.bookkeeping.view.chart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.LayoutChartDetailBinding
import com.example.bookkeeping.util.getFormattedRate
import java.time.LocalDate

class ChartDetailView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val binding: LayoutChartDetailBinding
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_chart_detail, this)
        binding = LayoutChartDetailBinding.bind(view)
    }

    fun setData(date: LocalDate, rate: Double){
        binding.dateTv.text = date.toString()
        binding.profitTv.text = context.getString(R.string.chart_detail_rate, getFormattedRate(rate))
    }

}