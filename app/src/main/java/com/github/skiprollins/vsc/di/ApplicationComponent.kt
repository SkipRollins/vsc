package com.github.skiprollins.vsc.di

import com.github.skiprollins.vsc.ui.CartFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    GsonModule::class,
    NetworkModule::class,
    ServiceModule::class,
    ContractModule::class
])
interface ApplicationComponent {

    fun inject(fragment: CartFragment)
}