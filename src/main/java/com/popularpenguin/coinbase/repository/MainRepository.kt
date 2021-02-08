package com.popularpenguin.coinbase.repository

import kotlinx.coroutines.flow.Flow

class MainRepository(private val client: CoinbaseClient) {

    fun connect() {
        client.connect()
    }

    fun disconnect() {
        client.disconnect()
    }

    fun getTicker(): Flow<CoinTicker?> {
        return client.ticker
    }
}