package com.github.skiprollins.vsc.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface InventoryApi {

    @GET("products")
    fun getAllProducts(): Single<List<Product>>

    @GET("product")
    fun getProductById(@Query("id") id: String): Single<Product>
}