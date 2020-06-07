package com.sizeofanton.flageolet

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData

interface MainContract {
    interface View

    interface ViewModel {
        fun updateFrequency(frequency: Double)
        fun updateAmplitude(amplitude: Int)
        fun updateDecibel(decibel: Double)
        fun updateNote(note: String)
        fun updatePosition(position: Int)
        fun startRecording()
        fun startRecording(delay: Long)
        fun stopRecording()
        fun getFrequency(): LiveData<Double>
        fun getAmplitude(): LiveData<Int>
        fun getDecibel(): LiveData<Double>
        fun getNote(): LiveData<String>
        fun getPosition(): LiveData<Int>
        fun setConfig(
            workMode: MainModel.WorkMode,
            frequencies: DoubleArray,
            names: Array<String>,
            string: Int
        )
    }

    interface Model {
        fun startRecording(delay: Long)
        fun stopRecording()
        fun setWorkMode(mode: MainModel.WorkMode)
        fun setSystem(frequencies: DoubleArray, names: Array<String>)
        fun setCurrentString(string: Int)
    }

}