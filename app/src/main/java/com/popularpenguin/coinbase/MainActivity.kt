package com.popularpenguin.coinbase

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.popularpenguin.coinbase.databinding.ActivityMainBinding
import com.popularpenguin.coinbase.repository.CoinTicker
import com.popularpenguin.coinbase.util.Format
import com.popularpenguin.coinbase.viewmodel.MainViewModel
import com.popularpenguin.coinbase.viewmodel.MainViewModel.TickerUIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect // needed for Flow collect function to register correctly
import kotlinx.coroutines.Job

// Simple Coinbase web socket ticker viewer with Kotlin Flow, Hilt, and MVVM architecture

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var job: Job
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        viewModel.connect()
        subscribeViews()
    }

    override fun onStop() {
        viewModel.disconnect()
        unsubscribeViews()

        super.onStop()
    }

    private fun subscribeViews() {
        job = lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is TickerUIState.Success -> showTickerData(uiState.ticker)
                    is TickerUIState.Error -> showError(uiState.exception)
                }
            }
        }
    }

    private fun showTickerData(coinTicker: CoinTicker?) {
        coinTicker?.let { ticker ->
            val formattedPrice = Format.formatUS(ticker.price)

            when (ticker.id) {
                "BTC-USD" -> binding.btcPriceView.text = "BTC: $formattedPrice"
                "ETH-USD" -> binding.ethPriceView.text = "ETH: $formattedPrice"
                "LTC-USD" -> binding.ltcPriceView.text = "LTC: $formattedPrice"
            }
        }
    }

    private fun showError(exception: Throwable) {
        // ...
    }

    private fun unsubscribeViews() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}