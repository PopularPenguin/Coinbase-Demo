package com.popularpenguin.coinbase.repository

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLSocketFactory

@ExperimentalCoroutinesApi
class CoinbaseClient(private val uri: String, private val moshi: Moshi) {

    private val TAG = "CoinbaseClient"

    var ticker = MutableStateFlow<CoinTicker?>(null)
        private set

    private lateinit var client: WebSocketClient

    fun connect() {
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

        client = createWebSocketClient(URI(uri)).apply {
            setSocketFactory(socketFactory)
            connect()
        }
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
                setUpPriceText(message)
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
                    "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-USD\", \"ETH-USD\"] }]\n" +
                    "}"
        )
    }

    private fun setUpPriceText(message: String?) {
        message?.let {
            val adapter: JsonAdapter<CoinTicker> = moshi.adapter(CoinTicker::class.java)
            val coin = adapter.fromJson(message)

            ticker.value = coin
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