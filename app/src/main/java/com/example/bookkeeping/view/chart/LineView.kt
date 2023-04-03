package com.example.bookkeeping.view.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class LineView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var linePaint: Paint? = null
    private val path = Path()


    override fun onDraw(canvas: Canvas) {
        linePaint?.let { paint ->
            paint.strokeWidth = height.toFloat()
            path.reset()
            path.moveTo(0f, height / 2f)
            path.lineTo(width.toFloat(), height / 2f)
            canvas.drawPath(path, paint)
        }
    }

    fun initPaint(color: Int, pathEffect: PathEffect? = null) {
        linePaint = Paint().apply {
            this.color = color
            isAntiAlias = true
            style = Paint.Style.STROKE
            pathEffect?.let {
                this.pathEffect = it
            }
        }
        invalidate()
    }


}