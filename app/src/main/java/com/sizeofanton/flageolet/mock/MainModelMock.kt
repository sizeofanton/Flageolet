package com.sizeofanton.flageolet.mock

import com.sizeofanton.flageolet.MainContract
import com.sizeofanton.flageolet.MainModel
import com.sizeofanton.flageolet.utils.GuitarFrequencies
import org.koin.core.KoinComponent
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

class MainModelMock(
    private val viewModel: MainContract.ViewModel
): MainContract.Model, KoinComponent {

    private val noteStep = 1.059461
    private lateinit var recordTimer: Timer

    private var frequencies = GuitarFrequencies.frequencies[0]?.first!!
    private var names = GuitarFrequencies.frequencies[0]?.second!!

    private var workMode: MainModel.WorkMode = MainModel.WorkMode.ALL_NOTES
    private var currentString: Int = -1

    override fun startRecording(delay: Int) {
        recordTimer = fixedRateTimer("recordTimer", false, delay.toLong(), 100) {
            record()
        }
    }

    override fun stopRecording() {
        if (this::recordTimer.isInitialized) recordTimer.cancel()
    }

    override fun setWorkMode(mode: MainModel.WorkMode) {
        workMode = mode
    }

    override fun setSystem(frequencies: DoubleArray, names: Array<String>) {
        TODO("Not yet implemented")
    }

    override fun setCurrentString(string: Int) {
       currentString = string
    }

    private fun record() {
        val freq = 329.63
        val amp = 320
        val db = -10.0
        val note = "E4"

        viewModel.updateFrequency(freq)
        viewModel.updateAmplitude(amp)
        viewModel.updateDecibel(db)
        viewModel.updateNote(note)
        viewModel.updatePosition(0)
    }

    enum class WorkMode {
        ALL_NOTES,
        SPECIFIC_NOTE
    }

    private fun getClosestNote(hz: Double): Int {
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

    private fun calculatePosition(requiredHz: Double, currentHz: Double): Int {
        val sign = (requiredHz - currentHz) < 0
        val next = if (sign) requiredHz * noteStep else requiredHz / noteStep
        return (abs(currentHz - requiredHz) * 100 / abs(requiredHz - next)).toInt() * if (sign) 1 else -1
    }

    fun testGetClosestNote(freq: Double): String {
        val closest = getClosestNote(freq)
        return names[closest]
    }

    fun testPosition(requiredHz: Double, currentHz: Double)
            = calculatePosition(requiredHz, currentHz)


}