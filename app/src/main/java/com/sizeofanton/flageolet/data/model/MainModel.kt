package com.sizeofanton.flageolet.data.model

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import be.tarsos.dsp.pitch.FastYin
import com.sizeofanton.flageolet.contract.MainContract
import com.sizeofanton.flageolet.data.local.GuitarFrequencies
import com.sizeofanton.flageolet.utils.dsp.AudioCalculator
import com.sizeofanton.flageolet.utils.NoteCalculator
import com.sizeofanton.flageolet.utils.PCMArrayConverter
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val FREQ_THRESHOLD_BOTTOM = 60
private const val FREQ_THRESHOLD_UPPER = 1000
private const val AMP_THRESHOLD = 1200
private const val DECIBEL_THRESHOLD = -18

data class MicData(
    val note: String,
    val freq: Double,
    val amp: Int,
    val db: Double,
    val position: Int
)
open class MainModel : MainContract.Model, KoinComponent {

    private lateinit var audioRecord: AudioRecord
    private val audioCalculator: AudioCalculator by inject()
    private val sampleRate = 44100
    private var minSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private val noteCalculator: NoteCalculator by inject()
    private val frequencies = GuitarFrequencies.frequencies[0]?.first!!
    private val names = GuitarFrequencies.frequencies[0]?.second!!
    private var frequenciesSpecific = GuitarFrequencies.frequencies[0]?.first!!
    private var workMode: WorkMode = WorkMode.ALL_NOTES
    private var currentString: Int = -1
    private val converter = get<PCMArrayConverter>()
    private val detector = get<FastYin>() { parametersOf(sampleRate.toDouble(), minSize) }

    private lateinit var micDataProducer: Observable<MicData>

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
        micDataProducer = Observable.create(object: ObservableOnSubscribe<MicData> {
            val executor = Executors.newSingleThreadScheduledExecutor()
            override fun subscribe(emitter: ObservableEmitter<MicData>) {
                val future = executor.scheduleAtFixedRate({
                    if (!emitter.isDisposed) {
                       val data = record()
                        emitter.onNext(data)
                    }
                }, delay, 100, TimeUnit.MILLISECONDS)

                emitter.setCancellable { future.cancel(false) }
            }
        })
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .filter {it.freq > FREQ_THRESHOLD_BOTTOM && it.freq < FREQ_THRESHOLD_UPPER}
            .filter {it.amp > AMP_THRESHOLD}
            .filter {it.db > DECIBEL_THRESHOLD}
        
    }

    override fun stopRecording() {
        Timber.d("Stop recording...")
        if (this::audioRecord.isInitialized) audioRecord.stop()
    }

    private fun record(): MicData {
        val (freq, amp, db) = getInput()

        Timber.d("Freq: $freq")
        Timber.d("Amp: $amp")
        Timber.d("Decibel: $db")

        val closest = noteCalculator.getClosestNote(freq, frequencies)
        return when (workMode) {
            WorkMode.ALL_NOTES -> {
                MicData(
                    names[closest],
                    freq,
                    amp,
                    db,
                    noteCalculator.calculateDeviation(frequencies[closest], freq)
                )
            }

            WorkMode.SPECIFIC_NOTE -> {
                MicData(
                    names[closest],
                    freq,
                    amp,
                    db,
                    noteCalculator.calculateDeviation(frequenciesSpecific[currentString -1], freq)
                )
            }
        }
    }

    protected open fun getInput(): Triple<Double, Int, Double> {
        var read = 0
        val bytesBuffer = ByteArray(minSize) { 0 }
        val shortBuffer = ShortArray(minSize) { 0 }

        audioRecord.read(bytesBuffer, 0, minSize)
        read = audioRecord.read(shortBuffer, 0, minSize)
        return if (read > 0) {
            audioCalculator.setBytes(bytesBuffer)
            val convertedArray = FloatArray(minSize)
            converter.convert(shortBuffer, convertedArray)
            val freq = detector.getPitch(convertedArray)
                .pitch
                .toDouble()
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
        this.frequenciesSpecific = frequencies
    }

    override fun setCurrentString(string: Int) {
        currentString = string
    }

    override fun getProducer() = micDataProducer
}
