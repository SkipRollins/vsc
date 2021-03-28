package com.github.skiprollins.vsc.ui

import com.github.skiprollins.vsc.network.Product
import io.reactivex.rxjava3.core.Observable

interface CartContract {

    val inventoryObservable: Observable<List<Product>>

    fun fetchInventory()

}