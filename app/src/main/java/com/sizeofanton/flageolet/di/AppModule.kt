package com.sizeofanton.flageolet.di

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.filters.BandPass
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.pitch.FastYin
import com.sizeofanton.flageolet.contract.MainContract
import com.sizeofanton.flageolet.data.model.MainModel
import com.sizeofanton.flageolet.ui.MainViewModel
import com.sizeofanton.flageolet.utils.NoteCalculator
import com.sizeofanton.flageolet.utils.PCMArrayConverter
import com.sizeofanton.flageolet.utils.dsp.AudioCalculator
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
    single { AudioCalculator() }
    single { PCMArrayConverter() }
    single { (sampleRate: Float, readSize: Int) -> FastYin(sampleRate, readSize) }
    single { NoteCalculator() }
    single<MainContract.Model> { MainModel() }
    viewModel { MainViewModel() }
    single { (centreFreq: Float, width: Float, sampleRate: Float) ->
                BandPass(centreFreq, width, sampleRate)}
    single { (sampleRate: Float) -> TarsosDSPAudioFormat(sampleRate, 16, 2, true, false)}
    single { (sampleRate: Float) -> AudioEvent(get() { parametersOf(sampleRate)}) }
}
