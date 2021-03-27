package com.github.skiprollins.vsc.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    GsonModule::class,
    NetworkModule::class
])
interface ApplicationComponent {

}