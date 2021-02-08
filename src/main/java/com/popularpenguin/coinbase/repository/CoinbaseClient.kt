package com.popularpenguin.coinbase.repository

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLSocketFactory

class CoinbaseClient(uri: String) {

    private val TAG = "CoinbaseClient"

    var btcPrice = MutableStateFlow<String>("0")

    private val client: WebSocketClient

    init {
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

        client = createWebSocketClient(URI(uri)).apply {
            setSocketFactory(socketFactory)
        }
    }

    fun connect() {
        client.connect()
    }

    fun disconnect() {
        client.close()
    }

    private fun createWebSocketClient(coinbaseUri: URI): WebSocketClient {
        return object : WebSocketClient(coinbaseUri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                subscribe()
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage: $message")
                setUpBtcPriceText(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
                unsubscribe()
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }
        }
    }

    private fun subscribe() {
        client.send(
            "{\n" +
                    "    \"type\": \"subscribe\",\n" +
                    "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
                    "}"
        )
    }

    private fun setUpBtcPriceText(message: String?) {
        message?.let {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<BitcoinTicker> = moshi.adapter(BitcoinTicker::class.java)
            val bitcoin = adapter.fromJson(message)

            btcPrice.value = bitcoin?.price ?: "NA"
        }
    }

    private fun unsubscribe() {
        client.send(
            "{\n" +
                    "    \"type\": \"unsubscribe\",\n" +
                    "    \"channels\": [\"ticker\"]\n" +
                    "}"
        )
    }
}