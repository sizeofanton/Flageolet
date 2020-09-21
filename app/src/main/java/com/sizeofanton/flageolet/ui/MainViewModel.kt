package com.sizeofanton.flageolet.ui

import androidx.lifecycle.*
import com.sizeofanton.flageolet.contract.MainContract
import com.sizeofanton.flageolet.data.model.MainModel
import com.sizeofanton.flageolet.data.model.MicData
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class MainViewModel : ViewModel(),
    MainContract.ViewModel, KoinComponent {

    private val model: MainContract.Model by inject()
    private var subscription: Disposable? = null
    private val frequency: MutableLiveData<Double> = MutableLiveData()
    private val amplitude: MutableLiveData<Int> = MutableLiveData()
    private val decibel: MutableLiveData<Double> = MutableLiveData()
    private val note: MutableLiveData<String> = MutableLiveData()
    private val position: MutableLiveData<Int> = MutableLiveData()

    override fun startRecording(delay: Long) {
        model.startRecording(delay)
        subscription = model.getProducer().subscribe { data ->
            this@MainViewModel.note.postValue(data.note)
            this@MainViewModel.frequency.postValue(data.freq)
            this@MainViewModel.amplitude.postValue(data.amp)
            this@MainViewModel.decibel.postValue(data.db)
            this@MainViewModel.position.postValue(data.position)
        }
    }

    override fun stopRecording() {
        subscription?.dispose()
        model.stopRecording()
    }

    override fun getFrequency(): LiveData<Double> = frequency
    override fun getAmplitude(): LiveData<Int> = amplitude
    override fun getDecibel(): LiveData<Double> = decibel
    override fun getNote(): LiveData<String> = note
    override fun getPosition(): LiveData<Int> = position

    override fun setConfig(
        workMode: MainModel.WorkMode,
        frequencies: DoubleArray,
        names: Array<String>,
        string: Int
    ) {
        model.setWorkMode(workMode)
        model.setSystem(frequencies, names)
        model.setCurrentString(string)
    }
}
