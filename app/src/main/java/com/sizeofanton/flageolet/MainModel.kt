package com.sizeofanton.flageolet

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.sizeofanton.flageolet.utils.*
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

private const val FREQ_THRESHOLD = 60
private const val AMP_THRESHOLD = 300
private const val DECIBEL_THRESHOLD = -18

class MainModel(private val viewModel:MainContract.ViewModel): MainContract.Model, KoinComponent {

    private lateinit var audioRecord: AudioRecord
    private val audioCalculator: AudioCalculator by inject()
    private val sampleRate = 44100
    private var minSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private val noteCalculator:NoteCalculator by inject()
    private lateinit var recordTimer: Timer

    private val frequencies = GuitarFrequencies.frequencies[0]?.first!!
    private val names = GuitarFrequencies.frequencies[0]?.second!!

    private var frequencies_specific
            = GuitarFrequencies.frequencies[0]?.first!!


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
        recordTimer = fixedRateTimer("recordTimer", false, delay, 50) {
            record()
        }
    }

    override fun stopRecording() {
        Timber.d("Stop recording...")
        if (this::recordTimer.isInitialized) recordTimer.cancel()
        if (this::audioRecord.isInitialized) audioRecord.stop()
    }

    private fun record() {
        val (freq, amp, db) = getInput()

        Timber.d("Freq: $freq")
        Timber.d("Amp: $amp")
        Timber.d("Decibel: $db")

        when (workMode) {
            WorkMode.ALL_NOTES -> {
                val closest = noteCalculator.getClosestNote(freq, frequencies)
                val note = names[closest]

                if (db > DECIBEL_THRESHOLD && amp > AMP_THRESHOLD && freq > FREQ_THRESHOLD) {
                    viewModel.updateFrequency(freq)
                    viewModel.updateAmplitude(amp)
                    viewModel.updateDecibel(db)
                    viewModel.updateNote(note)
                    noteCalculator.calculateDeviation(frequencies[closest], freq).also {
                        viewModel.updatePosition(it)
                    }
                }
            }

            WorkMode.SPECIFIC_NOTE -> {
                val closest = noteCalculator.getClosestNote(freq, frequencies)
                val note = names[closest]

                if (db > DECIBEL_THRESHOLD && amp > AMP_THRESHOLD && freq > FREQ_THRESHOLD) {
                    viewModel.updateFrequency(freq)
                    viewModel.updateNote(note)
                    noteCalculator.calculateDeviation(frequencies_specific[currentString-1], freq)
                        .also {
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

    enum class WorkMode {
        ALL_NOTES,
        SPECIFIC_NOTE
    }

    override fun setWorkMode(mode: WorkMode) {
        this.workMode = mode
    }

    override fun setSystem(frequencies: DoubleArray, names: Array<String>) {
        this.frequencies_specific = frequencies
    }

    override fun setCurrentString(string: Int) {
        currentString = string
    }
}