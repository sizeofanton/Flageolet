package com.sizeofanton.flageolet.di

import com.sizeofanton.flageolet.MainContract
import com.sizeofanton.flageolet.MainViewModel
import com.sizeofanton.flageolet.mock.MainModelMock
import org.koin.dsl.module

val mockModule = module {
    single<MainContract.Model> { MainModelMock(get()) }
    single<MainContract.ViewModel> {MainViewModel()}
}