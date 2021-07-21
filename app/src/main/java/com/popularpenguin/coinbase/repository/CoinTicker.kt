package com.popularpenguin.coinbase.repository

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinTicker(
    @Json(name = "product_id") val id: String?,
    val price: String?
)