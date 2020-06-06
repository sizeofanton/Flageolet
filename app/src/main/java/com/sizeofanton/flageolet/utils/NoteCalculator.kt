package com.sizeofanton.flageolet.utils

import kotlin.math.abs

private const val NOTE_STEP = 1.059461

class NoteCalculator {

    fun getClosestNote(hz: Double, frequencies: DoubleArray): Int {
        var minDist = Double.MAX_VALUE
        var minFreq = -1
        for (i in frequencies.indices) {
            val dist = Math.abs(frequencies[i] - hz)
            if (dist < minDist) {
                minDist = dist
                minFreq = i
            }
        }
        return minFreq
    }

    fun calculateDeviation(requiredHz: Double, currentHz: Double): Int {
        val sign = (requiredHz - currentHz) < 0
        val next = if (sign) requiredHz * NOTE_STEP else requiredHz / NOTE_STEP
        return (abs(currentHz - requiredHz) * 100 / abs(requiredHz - next)).toInt() * if (sign) 1 else -1
    }

}