package com.sizeofanton.flageolet

import android.app.Application
import com.sizeofanton.flageolet.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        startKoin {
            if (BuildConfig.DEBUG) androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}
