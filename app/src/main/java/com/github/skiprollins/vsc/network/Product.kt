package com.github.skiprollins.vsc.network

data class Product (
    val id: String,
    val qrUrl: String,
    val thumbnail: String,
    val name: String,
    val price: String
) {

    val priceValue: Double
        get() {
            val start = if (price.isNotEmpty() && price[0] == '$') 1 else 0
            val trimmed = price.substring(start)
            val value = trimmed.toDoubleOrNull()
            return value ?: 0.0
        }
}