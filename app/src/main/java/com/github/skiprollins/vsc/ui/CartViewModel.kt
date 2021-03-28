package com.github.skiprollins.vsc.ui

import com.github.skiprollins.vsc.network.InventoryService
import com.github.skiprollins.vsc.network.Product
import com.github.skiprollins.vsc.util.AutoDisposable
import com.github.skiprollins.vsc.util.SchedulerProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


class CartViewModel(
    private val inventoryService: InventoryService,
    private val schedulers: SchedulerProvider
): CartContract {

    private var sub by AutoDisposable()

    private val inventorySubject = PublishSubject.create<List<Product>>()
    override val inventoryObservable: Observable<List<Product>>
        get() = inventorySubject.observeOn(schedulers.main())

    private val itemSubject = PublishSubject.create<Product>()
    override val itemObservable: Observable<Product>
        get() = itemSubject.observeOn(schedulers.main())

    private val errorSubject = PublishSubject.create<Pair<CartContract.Error, Any?>>()
    override val errorObservable: Observable<Pair<CartContract.Error, Any?>>
        get() = errorSubject.observeOn(schedulers.main())

    override fun fetchInventory() {
        sub = inventoryService
            .getAllProducts()
            .map {
                when {
                    it.isSuccess -> it.getOrDefault(listOf())
                    else -> listOf()
                }
            }
            .subscribe { list -> inventorySubject.onNext(list) }
    }

    override fun getItem(id: String) {
        sub = inventoryService
            .getProductById(id)
            .subscribe { result ->
                when {
                    result.isSuccess -> result.getOrNull()?.let {
                        itemSubject.onNext(it)

                    } ?: errorSubject.onNext(CartContract.Error.ITEM_NOT_FOUND to id)

                    else -> errorSubject.onNext(CartContract.Error.ITEM_NOT_FOUND to id)
                }
            }
    }
}