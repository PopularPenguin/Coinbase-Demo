package com.popularpenguin.coinbase

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.popularpenguin.coinbase.databinding.ActivityMainBinding
import com.popularpenguin.coinbase.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// https://medium.com/@fahri.c93/android-tutorial-part-2-using-java-websocket-with-kotlin-dd3105bd3eed
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val jobs = mutableListOf<Job>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        subscribeViews()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
        unsubscribeViews()
    }

    private fun subscribeViews() {
        val btcJob = CoroutineScope(IO).launch {
            viewModel.getBtcPrice().collect { price ->
                withContext(Main) {
                    binding.btcPriceTv.text = "1 BTC: $$price"
                }
            }
        }

        jobs.add(btcJob)
    }

    private fun unsubscribeViews() {
        for (job in jobs) {
            job.cancel()
        }
        jobs.clear()
    }
}