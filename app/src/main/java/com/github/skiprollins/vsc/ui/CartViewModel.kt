package com.github.skiprollins.vsc.ui

import com.github.skiprollins.vsc.network.InventoryService
import com.github.skiprollins.vsc.network.Product
import com.github.skiprollins.vsc.util.AutoDisposable
import com.github.skiprollins.vsc.util.SchedulerProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


class CartViewModel(
    private val inventoryService: InventoryService,
    private val schedulers: SchedulerProvider
): CartContract {

    private var inventoryDisposable by AutoDisposable()
    private val inventorySubject = PublishSubject.create<List<Product>>()
    override val inventoryObservable: Observable<List<Product>>
        get() = inventorySubject.observeOn(schedulers.main())

    override fun fetchInventory() {
        inventoryDisposable = inventoryService
            .getAllProducts()
            .map {
                when {
                    it.isSuccess -> it.getOrDefault(listOf())
                    else -> listOf()
                }
            }
            .subscribe { list -> inventorySubject.onNext(list) }
    }
}