package com.example.chris.canvasdraw.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.chris.canvasdraw.model.Coordinates
import com.example.chris.canvasdraw.model.ClockPoints
import com.example.chris.canvasdraw.model.TimeMark

/**
 * Created by Chris on 12/11/17.
 */
const val centerRadius: Int = 5
const val offsetIntervalAngle = 90
const val minDegrees = 0
const val maxDegrees = 360
const val intervalsCount = 12
const val minRotationAngle = maxDegrees / 60
const val intervalAngle = maxDegrees / intervalsCount
const val clockTickAnimationTime = 100L
class ClockView: View {
    private lateinit var secondsTickerPaint: Paint
    private lateinit var minutesTickerPaint: Paint
    private lateinit var TimeTextPaint: Paint
    private lateinit var clockTimeMarkPaint: Paint
    private lateinit var clockCenterPaint: Paint
    private lateinit var clockCoverPaint: Paint

    private lateinit var secondsTicker: ClockPoints
    private lateinit var minutesTicker: ClockPoints

    private lateinit var clockCoverPath: Path
    private var intervals: MutableList<Pair<ClockPoints, TimeMark>> = mutableListOf()

    private var finalWidth = 0
    private var finalHeight = 0
    private var radius = 0
    private var nextTickerAngle = 0
    private var currentTickerAngle = 0

    private var minutesCounter = 0
    private var secondsCounter = 0

    private var secondsRotation = 0.0
    private var minutesRotation = 0.0

    private var clockCenterX = 0f
    private var clockCenterY = 0f

    private var isInitialized = false

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {
        init()
    }

    private fun init() {
        secondsTickerPaint = Paint()
        secondsTickerPaint.isAntiAlias = true
        secondsTickerPaint.strokeCap = Paint.Cap.ROUND
        secondsTickerPaint.strokeWidth = 4f

        minutesTickerPaint = Paint()
        minutesTickerPaint.isAntiAlias = true
        minutesTickerPaint.strokeCap = Paint.Cap.ROUND
        minutesTickerPaint.strokeWidth = 8f

        clockCenterPaint = Paint()
        clockCenterPaint.isAntiAlias = true
        clockCenterPaint.style = Paint.Style.FILL
        clockCenterPaint.strokeWidth = 8f

        clockCoverPaint = Paint()
        clockCoverPaint.isAntiAlias = true
        clockCoverPaint.strokeCap = Paint.Cap.ROUND
        clockCoverPaint.style = Paint.Style.STROKE
        clockCoverPaint.strokeWidth = 8f

        TimeTextPaint = Paint()
        TimeTextPaint.isAntiAlias = true

        clockTimeMarkPaint = Paint()
        clockTimeMarkPaint.isAntiAlias = true
        clockTimeMarkPaint.style = Paint.Style.FILL
        clockTimeMarkPaint.strokeWidth = 8f
        clockTimeMarkPaint.strokeCap = Paint.Cap.ROUND

        clockCoverPath = Path()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        finalWidth = measuredWidth / 3
        finalHeight = measuredWidth / 3
        radius = finalWidth / 2
        setMeasuredDimension(finalWidth, finalHeight)

        if (!isInitialized) {
            isInitialized = true
            initialize()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //clock base view (circle)
        canvas?.drawPath(clockCoverPath, clockCoverPaint)

        //clock center point
        canvas?.drawCircle(
                clockCenterX,
                clockCenterY,
                centerRadius.toFloat(),
                clockCenterPaint)

        intervals.forEach {
            canvas?.drawLine(
                    it.first.startCoords.x.toFloat(),
                    it.first.startCoords.y.toFloat(),
                    it.first.endCoords.x.toFloat(),
                    it.first.endCoords.y.toFloat(),
                    clockTimeMarkPaint)

            canvas?.drawText(
                    it.second.timeText,
                    it.second.pos.x.toFloat(),
                    it.second.pos.y.toFloat(),
                    TimeTextPaint
            )
        }

        canvas?.drawLine(
                secondsTicker.startCoords.x.toFloat(),
                secondsTicker.startCoords.y.toFloat(),
                secondsTicker.endCoords.x.toFloat(),
                secondsTicker.endCoords.y.toFloat(),
                secondsTickerPaint)

        canvas?.drawLine(
                minutesTicker.startCoords.x.toFloat(),
                minutesTicker.startCoords.y.toFloat(),
                minutesTicker.endCoords.x.toFloat(),
                minutesTicker.endCoords.y.toFloat(),
                minutesTickerPaint
        )
    }

    fun initialize() {
        clockCenterX = (finalWidth / 2).toFloat()
        clockCenterY = (finalHeight / 2).toFloat()

        val rect = RectF(16f, 16f, finalWidth.toFloat() - 16, finalHeight.toFloat() - 16)
        clockCoverPath.arcTo(rect, minDegrees.toFloat(), (maxDegrees - 1).toFloat(), true)

        setTickerPos(-offsetIntervalAngle, -offsetIntervalAngle)

        TimeTextPaint.textSize = 0.08f * finalWidth

        if (intervals.size == 0) {
            for (i in 0 until intervalsCount) {
                val rotationRadian = getRadianAngle(-offsetIntervalAngle + intervalAngle + (i * intervalAngle))

                val startCoord = Coordinates(
                        getXPoint(radius - 16, rotationRadian).toInt(),
                        getYPoint( radius - 16, rotationRadian).toInt()
                )

                val endCoord = Coordinates(
                        getXPoint(radius - 26, rotationRadian).toInt(),
                        getYPoint(radius - 26, rotationRadian).toInt()
                )

                val clockPoints = ClockPoints(startCoord, endCoord)
                val time = (i + 1).toString()
                val timePos = Coordinates(
                        (getXPoint(radius - 50, rotationRadian)
                                - (TimeTextPaint.measureText(time) / 2)).toInt(),//center of the text x
                        (getYPoint(radius - 50, rotationRadian)
                                - ((TimeTextPaint.descent() + TimeTextPaint.ascent()) / 2)).toInt() //center of the text y
                )

                intervals.add(Pair(clockPoints, TimeMark(time, timePos)))
            }
        }
    }

    private fun setTickerPos(secondAngle: Int, minuteAngle: Int) {
        //set point to 12
        val rotationSecond = getRadianAngle(secondAngle)
        val rotationMinute = getRadianAngle(minuteAngle)

        //set up second ticker
        val secStartCoord = Coordinates(clockCenterX.toInt(), clockCenterY.toInt())
        val secEndCoord = Coordinates(
                getXPoint(radius - 50, rotationSecond).toInt(),
                getYPoint( radius - 50, rotationSecond).toInt()
        )
        secondsTicker = ClockPoints(secStartCoord, secEndCoord)

        //set up minute ticker
        val minuteStartCoord = Coordinates(clockCenterX.toInt(), clockCenterY.toInt())
        val minuteEndCoord = Coordinates(
                getXPoint(radius - 70, rotationMinute).toInt(),
                getYPoint( radius - 70, rotationMinute).toInt()
        )
        minutesTicker = ClockPoints(minuteStartCoord, minuteEndCoord)
    }

    private fun getRadianAngle(angle: Int): Double {
        return angle * (Math.PI /  180)
    }

    private fun getXPoint(radius: Int, angle: Double): Double {
        return clockCenterX + (radius * Math.cos(angle))
    }

    private fun getYPoint(radius: Int, angle: Double): Double {
        return clockCenterY + (radius * Math.sin(angle))
    }

    fun tick(seconds: Int) {
        nextTickerAngle = seconds * minRotationAngle - offsetIntervalAngle

        val animator = ValueAnimator.ofInt(currentTickerAngle, nextTickerAngle)
        animator.duration = clockTickAnimationTime
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            setTickerPos(nextTickerAngle, -offsetIntervalAngle)
            invalidate()
        }

        currentTickerAngle = if (nextTickerAngle >= maxDegrees) minDegrees else nextTickerAngle
        animator.start()
    }

    fun reset() {
        setTickerPos(-offsetIntervalAngle, -offsetIntervalAngle)
        invalidate()
    }
}