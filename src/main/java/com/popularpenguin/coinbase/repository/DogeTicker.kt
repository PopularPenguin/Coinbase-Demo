package com.popularpenguin.coinbase.repository

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DogeTicker(val price: String?)