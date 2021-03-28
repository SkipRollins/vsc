package com.github.skiprollins.vsc.util

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.reflect.KProperty

class NoOpDisposable : Disposable {
    override fun dispose() { } // No - OP
    override fun isDisposed() = true
}

class AutoDisposable {
    var thisValue: Disposable = NoOpDisposable()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Disposable {
        return thisValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Disposable) {
        thisValue.dispose()
        thisValue = newValue
    }
}

interface SchedulerProvider {
    fun net(): Scheduler
    fun main(): Scheduler
}

class SchedulerProviderImpl: SchedulerProvider {

    override fun net(): Scheduler = Schedulers.io()
    override fun main(): Scheduler = AndroidSchedulers.mainThread()
}