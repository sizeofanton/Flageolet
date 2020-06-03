package com.sizeofanton.flageolet

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.sizeofanton.flageolet.utils.AudioCalculator
import com.sizeofanton.flageolet.utils.GuitarFrequencies
import com.sizeofanton.flageolet.utils.PCMArrayConverter
import com.sizeofanton.flageolet.utils.YINPitchDetector
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

class MainModel(private val viewModel:MainContract.ViewModel): MainContract.Model, KoinComponent {

    private lateinit var audioRecord: AudioRecord
    private val audioCalculator: AudioCalculator by inject()
    private val sampleRate = 44100
    private var minSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private val noteStep = 1.059461
    private lateinit var recordTimer: Timer

    private var frequencies = GuitarFrequencies.frequencies[0]?.first!!
    private var names = GuitarFrequencies.frequencies[0]?.second!!

    private var workMode: WorkMode = WorkMode.ALL_NOTES
    private var currentString: Int = -1


    override fun startRecording(delay: Long) {
        Timber.d("Start recording...")
        if (this::audioRecord.isInitialized) audioRecord.release()
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minSize
        )
        audioRecord.startRecording()
        recordTimer = fixedRateTimer("recordTimer", false, delay, 100) {
            record()
        }
    }

    override fun stopRecording() {
        Timber.d("Stop recording...")
        if (this::recordTimer.isInitialized) recordTimer.cancel()
        if (this::audioRecord.isInitialized) audioRecord.stop()
    }

    private fun record() {
        Timber.d("Thread name - ${Thread.currentThread().id}")
        val (freq, amp, db) = getInput()

        Timber.d("Freq: $freq")
        Timber.d("Amp: $amp")
        Timber.d("Decibel: $db")
        //Timber.d("Recording status: ${audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING}")

        when (workMode) {
            WorkMode.ALL_NOTES -> {
                val closest = getClosestNote(freq)
                val note = names[closest]

                if (db > -18 && amp > 300 && freq > 60) {
                    viewModel.updateFrequency(freq)
                    viewModel.updateAmplitude(amp)
                    viewModel.updateDecibel(db)
                    viewModel.updateNote(note)
                    calculatePosition(frequencies[closest], freq).also {
                        viewModel.updatePosition(it)
                    }
                }
            }

            WorkMode.SPECIFIC_NOTE -> {
                val closest = frequencies[currentString - 1]
                val note = names[currentString - 1]

                if (db > - 12 && amp > 1200 && freq > 60) {
                    viewModel.updateFrequency(freq)
                    viewModel.updateNote(note)
                    calculatePosition(closest, freq).also {
                        viewModel.updatePosition(it)
                    }
                }
            }
        }


    }

    private fun getInput(): Triple<Double, Int, Double> {
        var read = 0
        val bytesBuffer = ByteArray(minSize){0}
        val shortBuffer = ShortArray(minSize){0}

        audioRecord.read(bytesBuffer, 0, minSize)
        read = audioRecord.read(shortBuffer, 0, minSize)
        return if (read > 0) {
            audioCalculator.setBytes(bytesBuffer)
            val converter = get<PCMArrayConverter>()
            val convertedArray = FloatArray(minSize)
            converter.convert(shortBuffer, convertedArray)
            val detector = get<YINPitchDetector>() { parametersOf(sampleRate.toDouble(), minSize) }

            val freq = detector.detect(convertedArray)
            val amp = audioCalculator.amplitude
            val db = audioCalculator.decibel

            return Triple(freq, amp, db)
        } else Triple(0.0, 0, 0.0)
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

    enum class WorkMode {
        ALL_NOTES,
        SPECIFIC_NOTE
    }

    override fun setWorkMode(mode: WorkMode) {
        this.workMode = mode
    }

    override fun setSystem(frequencies: DoubleArray, names: Array<String>) {
        this.frequencies = frequencies
        this.names = names
    }

    override fun setCurrentString(string: Int) {
        currentString = string
    }
}