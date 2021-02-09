package com.popularpenguin.coinbase.repository

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subscribe(
    val type: String = "subscribe",
    @Json(name = "product_ids")
    val productIds: List<String>,
    val channels: List<String>
)

val BITCOIN_SUBSCRIBE_MESSAGE = Subscribe(
    productIds = listOf("BTC-USD"),
    channels = listOf("ticker")
)
val ETHEREUM_SUBSCRIBE_MESSAGE = Subscribe(
    productIds = listOf("ETH-USD"),
    channels = listOf("ticker")
)
val LITECOIN_SUBSCRIBE_MESSAGE = Subscribe(
    productIds = listOf("LTC-USD"),
    channels = listOf("ticker")
)
val ALL_SUBSCRIBE_MESSAGE = Subscribe(
    productIds = listOf("BTC-USD", "ETH-USD", "LTC-USD"),
    channels = listOf("ticker")
)

@JsonClass(generateAdapter = true)
data class Unsubscribe(
    val type: String = "unsubscribe",
    val channels: String = "ticker"
)

val UNSUBSCRIBE_MESSAGE = Unsubscribe()



