package com.example.bookkeeping.view.chart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView

import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.LayoutChartBinding
import java.time.LocalDate

class ChartLayout(context: Context, attrs: AttributeSet) : CardView(context, attrs),
    ChartView.PointSelectedListener {

    private val binding: LayoutChartBinding

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_chart, this)
        binding = LayoutChartBinding.bind(view)
        binding.chartView.setPointSelectedListener(this)
        binding.legendLayout.initLegend(
            context.getString(R.string.legend_rate),
            context.getColor(R.color.light_blue_600)
        )
    }


    override fun onPointSelected(date: LocalDate, rate: Double) {
        binding.detailLayout.visibility = View.VISIBLE
        binding.detailView.setData(date, rate)
    }

    override fun onPointCanceled() {
        binding.detailLayout.visibility = View.GONE
    }

    fun setData(data: List<Pair<LocalDate, Double>>) {
        binding.chartView.setData(data)
    }
}