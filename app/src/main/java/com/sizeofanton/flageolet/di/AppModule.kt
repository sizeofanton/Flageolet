package com.sizeofanton.flageolet.di

import com.sizeofanton.flageolet.MainContract
import com.sizeofanton.flageolet.MainModel
import com.sizeofanton.flageolet.MainViewModel
import com.sizeofanton.flageolet.utils.AudioCalculator
import com.sizeofanton.flageolet.utils.NoteCalculator
import com.sizeofanton.flageolet.utils.PCMArrayConverter
import com.sizeofanton.flageolet.utils.YINPitchDetector
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AudioCalculator() }
    single { PCMArrayConverter() }
    single { (sampleRate: Double, readSize: Int) -> YINPitchDetector(sampleRate, readSize) }
    single { NoteCalculator() }
    single<MainContract.Model> { (vm: MainContract.ViewModel) -> MainModel(vm) }
    viewModel { MainViewModel() }
}
