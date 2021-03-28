package com.github.skiprollins.vsc.network

import com.github.skiprollins.vsc.util.SchedulerProvider
import io.reactivex.rxjava3.core.Single
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

    fun getProductById(id: String): Single<Result<Product>> {
        return api.getProductById(id)
            .subscribeOn(schedulers.net())
            .map { Result.success(it) }
            .onErrorReturn {
                Timber.e(it)
                Result.failure(it)
            }

    }
}