package com.popularpenguin.coinbase.repository

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLSocketFactory

// Coinbase websocket
class CoinbaseClient(private val uri: String, private val moshi: Moshi) {

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
                subscribe()
            }

            override fun onMessage(message: String?) {
                setUpPriceText(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                unsubscribe()
            }

            override fun onError(ex: Exception?) {}
        }
    }

    private fun subscribe() {
        val adapter: JsonAdapter<Subscribe> = moshi.adapter(Subscribe::class.java)
        val message = adapter.toJson(ALL_SUBSCRIBE_MESSAGE)

        client.send(message)
    }

    private fun setUpPriceText(message: String?) {
        message?.let {
            val adapter: JsonAdapter<CoinTicker> = moshi.adapter(CoinTicker::class.java)
            val coin = adapter.fromJson(message)

            ticker.value = coin
        }
    }

    private fun unsubscribe() {
        val adapter : JsonAdapter<Unsubscribe> = moshi.adapter(Unsubscribe::class.java)
        val unsubscribeMessage = adapter.toJson(UNSUBSCRIBE_MESSAGE)

        client.send(unsubscribeMessage)
    }
}