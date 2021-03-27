package com.github.skiprollins.vsc

import android.app.Application
import android.content.Context
import com.github.skiprollins.vsc.di.*
import timber.log.Timber

class BaseApp: Application() {

    lateinit var appComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()
        initDI()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initDI() {
        appComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .gsonModule(GsonModule())
            .networkModule(NetworkModule())
            .build()
    }

}