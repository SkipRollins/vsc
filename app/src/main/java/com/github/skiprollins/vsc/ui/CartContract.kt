package com.github.skiprollins.vsc.ui

import com.github.skiprollins.vsc.network.Product
import io.reactivex.rxjava3.core.Observable

interface CartContract {

    enum class Error {
        ITEM_NOT_FOUND
    }

    val inventoryObservable: Observable<List<Product>>

    val itemObservable: Observable<Product>

    val errorObservable: Observable<Pair<Error, Any?>>

    fun fetchInventory()

    fun getItem(id: String)

}