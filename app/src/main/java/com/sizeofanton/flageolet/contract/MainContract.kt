package com.sizeofanton.flageolet.contract

import androidx.lifecycle.LiveData
import com.sizeofanton.flageolet.data.model.MainModel
import com.sizeofanton.flageolet.data.model.MicData
import io.reactivex.Observable

interface MainContract {
    interface View

    interface ViewModel {
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
        fun getProducer(): Observable<MicData>
    }
}
