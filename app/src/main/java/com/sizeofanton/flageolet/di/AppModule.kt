package com.sizeofanton.flageolet.di

import be.tarsos.dsp.pitch.FastYin
import com.sizeofanton.flageolet.contract.MainContract
import com.sizeofanton.flageolet.data.model.MainModel
import com.sizeofanton.flageolet.ui.MainViewModel
import com.sizeofanton.flageolet.utils.dsp.AudioCalculator
import com.sizeofanton.flageolet.utils.NoteCalculator
import com.sizeofanton.flageolet.utils.PCMArrayConverter
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AudioCalculator() }
    single { PCMArrayConverter() }
    single { (sampleRate: Float, readSize: Int) -> FastYin(sampleRate, readSize) }
    single { NoteCalculator() }
    single<MainContract.Model> { (vm: MainContract.ViewModel) ->
        MainModel(
            vm
        )
    }
    viewModel { MainViewModel() }
}
