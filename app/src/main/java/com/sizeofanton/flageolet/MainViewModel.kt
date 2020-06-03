package com.sizeofanton.flageolet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class MainViewModel: ViewModel(), MainContract.ViewModel, KoinComponent  {

    private val model: MainContract.Model by inject { parametersOf(this) }
    private val frequency: MutableLiveData<Double> = MutableLiveData()
    private val amplitude: MutableLiveData<Int> = MutableLiveData()
    private val decibel: MutableLiveData<Double> = MutableLiveData()
    private val note: MutableLiveData<String> = MutableLiveData()
    private val position: MutableLiveData<Int> = MutableLiveData()

    override fun updateFrequency(frequency: Double) {
        Timber.d("Updated frequency $frequency")
        this.frequency.postValue(frequency)
    }

    override fun updateAmplitude(amplitude: Int) {
        this.amplitude.postValue(amplitude)
    }

    override fun updateDecibel(decibel: Double) {
        this.decibel.postValue(decibel)
    }

    override fun updateNote(note: String) {
        Timber.d("Updated note $note")
        this.note.postValue(note)
    }

    override fun updatePosition(position: Int) {
        Timber.d("Updated position $position")
        this.position.postValue(position)
    }

    override fun startRecording(delay: Long) {
        model.startRecording(delay)
    }

    override fun stopRecording() {
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