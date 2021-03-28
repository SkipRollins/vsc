package com.github.skiprollins.vsc.network

import com.github.skiprollins.vsc.util.SchedulerProvider
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class InventoryService(
    private val api: InventoryApi,
    private val schedulers: SchedulerProvider
) {
    fun getAllProducts(): Single<Result<List<Product>>> {
        return api.getAllProducts()
            .subscribeOn(schedulers.net())
            .map { Result.success(it) }
            .onErrorReturn {
                Timber.e(it)
                Result.failure(it)
            }

    }
}