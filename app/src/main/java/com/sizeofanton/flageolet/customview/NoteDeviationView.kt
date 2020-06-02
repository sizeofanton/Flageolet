package com.sizeofanton.flageolet.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.Exception


class NoteDeviationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttrs: Int = 0) : View(context, attrs, defStyleAttrs){

    private val labels = arrayOf("+50", "+40", "+30", "+20", "+10", "  0",
                                              "-10", "-20", "-30", "-40", "-50")

    private var primaryLineWidth = 20.0f
    private var secondaryLineWidth = 10.0f

    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(217, 0x22, 0x21, 0x21)
        strokeWidth = secondaryLineWidth
    }

    private val centralPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 0x31, 0xD8, 0x12)
        strokeWidth = primaryLineWidth
    }

    private val frontierPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 0xFE, 0xED, 0x00)
        strokeWidth = secondaryLineWidth
    }

    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 0x97, 0x97, 0x97)
        strokeWidth = secondaryLineWidth
    }

    private val fontPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 50f
        style = Paint.Style.STROKE
    }

    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = markPaint.color
        strokeWidth = primaryLineWidth
    }

    private var pointerPosition = 0.0f
    private var pointerVisible = true

    fun setPointerVisibility(isVisible: Boolean) {
        pointerVisible = isVisible
    }

    fun setPosition(pos: Int) {
        if (pos !in -50..50) throw Exception("WrongIntervalException")
        pointerPosition = when (pos) {
            0 -> 11.0f / 22.0f
            else -> (11.0f / 22.0f) - (10.0f / 22.0f * pos / 50.0f)
        }
        invalidate()
    }

    fun setPointerColor(alpha: Int, r: Int, g: Int, b: Int) {
        pointerPaint.color = Color.argb(alpha, r, g, b)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        primaryLineWidth = height / 107.5f
        secondaryLineWidth = height / 215.0f
        reloadPaints()
        drawOutline(canvas)
        drawFrontierSectors(canvas)
        drawCentralSector(canvas)
        drawMarks(canvas)
        drawText(canvas)
        if (pointerVisible) drawPointer(canvas)
        super.onDraw(canvas)
    }


    private fun drawOutline(canvas: Canvas) {
        val rect = Rect(
                (width.toFloat() / 11).toInt(),
                (height.toFloat() / 22).toInt(),
                (9 * width.toFloat() / 11).toInt(),
                (21 * height.toFloat() / 22).toInt()
        )
        canvas.drawRect(rect, outlinePaint)
    }

    private fun drawCentralSector(canvas: Canvas) {
        canvas.drawLine(
                width.toFloat()/11,
                11 * height.toFloat() / 22,
                9 * width.toFloat() / 11,
                11 * height.toFloat() / 22,
                centralPaint
        )
    }

    private fun drawFrontierSectors(canvas: Canvas) {
        val rect = Rect(
                (width.toFloat() / 11).toInt(),
                (9 * height.toFloat() / 22).toInt(),
                (9 * width.toFloat() / 11).toInt(),
                (13 * height.toFloat() / 22).toInt()
        )
        canvas.drawRect(rect, frontierPaint)
    }

    private fun drawMarks(canvas: Canvas) {
        for (i in 0..9)
            canvas.drawLine(
                width.toFloat() / 11,
                    (2+2*i) * height.toFloat() / 22,
                2 * width.toFloat() / 11,
                    (2+2*i) * height.toFloat() / 22,
                markPaint
        )

        for (i in 0..9)
            canvas.drawLine(
                    8 * width.toFloat() / 11,
                    (2+2*i) * height.toFloat() / 22,
                    9 * width.toFloat() / 11,
                    (2+2*i) * height.toFloat() / 22,
                    markPaint
            )


        for (i in 1..10)
            canvas.drawLine(
                    width.toFloat() / 11,
                    (2f * i + 0.5f) * height.toFloat() / 22,
                    1.5f * width.toFloat() / 11,
                    (2f * i + 0.5f) * height.toFloat() / 22,
                    markPaint
            )

        for (i in 1..10)
            canvas.drawLine(
                    width.toFloat() / 11,
                    (2f * i - 0.5f) * height.toFloat() / 22,
                    1.5f * width.toFloat() / 11,
                    (2f * i - 0.5f) * height.toFloat() / 22,
                    markPaint
            )

        for (i in 1..10)
            canvas.drawLine(
                    8.5f * width.toFloat() / 11,
                    (2f * i + 0.5f) * height.toFloat() / 22,
                    9f * width.toFloat() / 11,
                    (2f * i + 0.5f) * height.toFloat() / 22,
                    markPaint
            )


        for (i in 1..10)
            canvas.drawLine(
                    8.5f * width.toFloat() / 11,
                    (2f * i - 0.5f) * height.toFloat() / 22,
                    9f * width.toFloat() / 11,
                    (2f * i - 0.5f) * height.toFloat() / 22,
                    markPaint
            )

    }

    private fun drawText(canvas: Canvas) {
        fontPaint.textSize = height / 42f
        for (i in labels.indices)
           canvas.drawText(labels[i], 10f * width / 11, (1f + 2f * i) * height / 22, fontPaint)
    }

    private fun drawPointer(canvas: Canvas) {
        canvas.drawLine(
                width.toFloat() / 11,
                pointerPosition * height,
                9.0f * width.toFloat() / 11,
                pointerPosition * height,
                pointerPaint
        )
    }

    private fun reloadPaints() {
        outlinePaint.strokeWidth = secondaryLineWidth
        centralPaint.strokeWidth = primaryLineWidth
        frontierPaint.strokeWidth = secondaryLineWidth
        markPaint.strokeWidth = secondaryLineWidth
        pointerPaint.strokeWidth = primaryLineWidth
    }


}