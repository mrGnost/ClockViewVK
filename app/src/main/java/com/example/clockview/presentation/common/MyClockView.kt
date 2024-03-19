package com.example.clockview.presentation.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class MyClockView
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {
    private var radius = 0
    private var padding = 30
    private var fontSize = 14
    private var minuteHandTruncation = 50
    private var hourHandTruncation = 150
    private var currentHour = 0
    private var currentMinute = 0
    private val hours = List(12) { it + 1 }
    private var paint = Paint()
    private val rect = Rect()
    private lateinit var calendar: Calendar
    private var isInit = false

    init {
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            14f,
            resources.displayMetrics
        ).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInit) {
            radius = min(width, height) / 2
            isInit = true
        }
        paint.reset()
        paint.color = Color.WHITE
        setupClockBody(canvas)
        drawTime(canvas)
        postInvalidateDelayed(500)
        invalidate()
    }

    private fun setupClockBody(canvas: Canvas) {
        drawClockBorder(canvas)
        drawClockCenterPoint(canvas)
        drawClockNumbers(canvas)
    }

    private fun drawClockBorder(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.isAntiAlias = true
        Log.d("CANVAS", "width: $width, height: $height, radius: $radius")
        canvas.drawCircle(width / 2f, height / 2f, radius.toFloat() - 1f, paint)
    }

    private fun drawClockCenterPoint(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        canvas.drawCircle(width / 2f, height / 2f, 12f, paint)
    }

    private fun drawClockNumbers(canvas: Canvas) {
        paint.textSize = fontSize.toFloat()
        for (hour in hours) {
            val hourString = hour.toString()
            paint.getTextBounds(hourString, 0, hourString.length, rect)
            val angle = Math.PI / 6 * (hour - 3)
            canvas.drawText(
                hourString,
                (width / 2 + cos(angle) * (radius - padding) - rect.width() / 2).toFloat(),
                (height / 2 + sin(angle) * (radius - padding) + rect.height() / 2).toFloat(),
                paint
            )
        }
    }

    private fun drawTime(canvas: Canvas) {
        calendar = Calendar.getInstance()
        currentHour = (calendar.get(Calendar.HOUR_OF_DAY) - 1) % 12 + 1
        currentMinute = calendar.get(Calendar.MINUTE)
        drawHandLine(canvas, (currentHour + currentMinute / 60) * 5.0, TimeType.Hour)
        drawHandLine(canvas, currentMinute.toDouble(), TimeType.Minute)
        drawHandLine(canvas, calendar.get(Calendar.SECOND).toDouble(), TimeType.Second)
    }

    private fun drawHandLine(canvas: Canvas, moment: Double, timeType: TimeType) {
        val angle = Math.PI * moment / 30 - Math.PI / 2
        var handRadius = 0
        when (timeType) {
            TimeType.Hour -> handRadius = radius - hourHandTruncation
            TimeType.Minute -> handRadius = radius - minuteHandTruncation
            TimeType.Second -> {
                handRadius = radius - minuteHandTruncation
                paint.color = Color.YELLOW
            }
        }
        canvas.drawLine(
            width / 2f,
            height / 2f,
            (width / 2 + cos(angle) * handRadius).toFloat(),
            (height / 2 + sin(angle) * handRadius).toFloat(),
            paint
        )
    }

    enum class TimeType {
        Hour, Minute, Second
    }
}