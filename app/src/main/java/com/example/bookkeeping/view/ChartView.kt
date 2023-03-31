package com.example.bookkeeping.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.bookkeeping.R
import com.example.bookkeeping.util.getBaselineHeight
import com.example.bookkeeping.util.getTextHeight
import com.example.bookkeeping.util.getTextWidth
import java.time.LocalDate
import kotlin.math.ceil

class ChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    init {
        setOnTouchListener(DisallowInterceptTouchListener())
    }

    private val margin = 10f
    private val marginTop = 10f
        get() = if (field != 0f) field else margin
    private val marginBottom = 10f
        get() = if (field != 0f) field else margin
    private val marginStart = 10f
        get() = if (field != 0f) field else margin
    private val marginEnd = 10f
        get() = if (field != 0f) field else margin

    private val num = 4

    private val mLinePaint = Paint().apply {
        isAntiAlias = true
        color = context.getColor(R.color.light_blue_600)
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val path = Path()

    private val mHorizontalLinePaint = Paint().apply {
        isAntiAlias = true
        color = context.getColor(R.color.black)
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val mYAxisTextPaint = Paint().apply {
        isAntiAlias = true
        textSize = 30f
        color = Color.BLACK
        textAlign = Paint.Align.RIGHT
    }
    private val mYAxisTextWidth = 150f

    private val mTextPaint = Paint().apply {
        isAntiAlias = true
        textSize = 50f
        color = Color.BLACK
        strokeWidth = 5f
    }

    private val mDottedLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
    }
    private val mOuterCirclePaint = Paint().apply {
        isAntiAlias = true
        color = context.getColor(R.color.light_blue_600)
        style = Paint.Style.FILL
        strokeWidth = 5f
    }
    private var r1 = 5f
    private var r2 = 10f
    private val mInnerCirclePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
        strokeWidth = 5f
    }

    private var data: List<Pair<LocalDate, Double>>? = null
    private var transformedData: List<Pair<Float, Float>>? = null
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private var maxRate: Double = 0.0
    private var minRate: Double = 0.0
    private var unit = 1
    private var yUpRate = 0
    private var draw = false
    private var focusX = 0f
    private var focusY = 0f
    private var focused = false

    private var focusBlock: ((LocalDate, Double) -> Unit)? = null


    override fun onDraw(canvas: Canvas) {
        if (draw) {
            drawLine(canvas)
            if (focused) {
                drawFocus(canvas)
            }
        } else {
            drawLaterShowText(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                setFocus(event.x, event.y)
                true
            }
            MotionEvent.ACTION_UP -> {
                focused = false
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    fun setData(data: List<Pair<LocalDate, Double>>) {
        this.data = data
        transformData(data)
        invalidate()
    }


    fun setFocusBlock(focusBlock: ((LocalDate, Double) -> Unit)?) {
        this.focusBlock = focusBlock
    }

    private fun drawLine(canvas: Canvas) {
        transformedData?.let {
            drawYAxis(canvas)
            path.reset()
            var data = it[0]
            var (x, y) = data
            Log.d(TAG, "start X:$x Y:$y")
            path.moveTo(x, y)
            for (i in 1..it.lastIndex) {
                data = it[i]
                x = data.first
                y = data.second
                Log.d(TAG, "X:$x Y:$y")
                path.lineTo(x, y)
            }
        }
        canvas.drawPath(path, mLinePaint)
    }

    private fun drawYAxis(canvas: Canvas) {
        for (i in 0..num) {
            val (text, textX, textY) = getYAxisTextAndPoint(i)
            canvas.drawText(text, textX, textY, mYAxisTextPaint)
            path.reset()
            val lineY = getYAxisLineY(i)
            path.moveTo(getChartStartX(), lineY)
            path.lineTo(getChartEndX(), lineY)
            canvas.drawPath(path, mHorizontalLinePaint)
        }
    }

    //todo text位置文字
    private fun drawLaterShowText(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val text = "Hello World"
        val textWidth = mTextPaint.getTextWidth(text)
        val textHeight = mTextPaint.getTextHeight()
        val textX = centerX - textWidth / 2f
        val textY = centerY + textHeight / 2f
        canvas.drawText(text, textX, textY, mTextPaint)
    }


    private fun setFocus(x: Float, y: Float) {
        transformedData?.let {
            if (!draw) {
                return
            }
            var lastX = it[0].first
            var lastY = it[0].second
            for ((posX, posY) in it) {
                if (x >= posX) {
                    lastX = posX
                    lastY = posY
                    continue
                }
                //离posX近
                if (lastX + posX < 2 * x) {
                    focusX = posX
                    focusY = posY
                } else {
                    focusX = lastX
                    focusY = lastY
                }
                focused = true
                invalidate()
                break
            }
        }
    }

    private fun drawFocus(canvas: Canvas) {
        path.reset()
        path.moveTo(focusX, getChartStartY())
        path.lineTo(focusX, getChartEndY())
        canvas.drawPath(path, mDottedLinePaint)
        path.reset()
        path.moveTo(getChartStartX(), focusY)
        path.lineTo(getChartEndX(), focusY)
        canvas.drawPath(path, mDottedLinePaint)
        canvas.drawCircle(focusX, focusY, r2, mOuterCirclePaint)
        canvas.drawCircle(focusX, focusY, r1, mInnerCirclePaint)
        transformedData?.let { pointList ->
            val index = pointList.indexOf(Pair(focusX,focusY))
            data?.let {
                val pair = it[index]
                focusBlock?.invoke(pair.first,pair.second)
            }
        }

    }


    private fun transformData(data: List<Pair<LocalDate, Double>>) {
        if (data.size <= 1) {
            return
        }
        startDate = data.first().first
        endDate = data.last().first
        computeYAxis(data)
        transformedData = data.map {
            val pair = Pair(getXFromDate(it.first), getYFromRate(it.second))
            val point = getDataPoint(pair)
            Pair(point.first, point.second)
        }
        Log.d(TAG, data.toString())
        Log.d(TAG, transformedData.toString())
        draw = true
        invalidate()
    }

    private fun computeYAxis(data: List<Pair<LocalDate, Double>>) {
        maxRate = data.maxOf { it.second }
        minRate = data.minOf { it.second }
        unit = ceil((maxRate - minRate) * 100 / (num - 1)).toInt()
        yUpRate = ((maxRate + minRate) * 100 / 2 + 2 * unit).toInt()
    }

    private fun getXFromDate(date: LocalDate): Float {
        val start = startDate?.toEpochDay() ?: return 0f
        val end = endDate?.toEpochDay() ?: return 0f
        return (date.toEpochDay() - start) * 1f / (end - start)
    }

    private fun getYFromRate(rate: Double): Float {
        return ((yUpRate - rate * 100) / (num * unit)).toFloat()
    }

    private fun getDataPoint(pair: Pair<Float, Float>): Pair<Float, Float> {
        val offsetX = pair.first * getChartWidth()
        val offsetY = pair.second * getChartHeight()
        return Pair(getChartStartX() + offsetX, getChartStartY() + offsetY)
    }

    private fun getYAxisTextAndPoint(index: Int): Triple<String, Float, Float> {
        val rate = yUpRate - unit * index
        val text = "$rate.00%"
        val textX = getChartStartX()
        val offsetY = getChartHeight() / num
        val textY = margin + mYAxisTextPaint.getBaselineHeight() + offsetY * index
        Log.d(
            TAG,
            "width:${mYAxisTextPaint.getTextWidth(text)}, height: ${mYAxisTextPaint.getTextHeight()}"
        )
        return Triple(text, textX, textY)
    }

    private fun getYAxisLineY(index: Int): Float {
        val offsetY = getChartHeight() / num
        return getChartStartY() + offsetY * index
    }

    private fun getChartStartX(): Float {
        return margin + mYAxisTextWidth
    }

    private fun getChartEndX(): Float {
        return width - margin
    }

    private fun getChartWidth(): Float {
        return getChartEndX() - getChartStartX()
    }

    private fun getChartStartY(): Float {
        return margin + mYAxisTextPaint.getTextHeight() / 2
    }

    private fun getChartEndY(): Float {
        return height - margin - mYAxisTextPaint.getTextHeight() / 2
    }

    private fun getChartHeight(): Float {
        return getChartEndY() - getChartStartY()
    }

    companion object {
        const val TAG = "ChartView"
    }

}