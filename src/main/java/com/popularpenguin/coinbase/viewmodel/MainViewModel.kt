package com.popularpenguin.coinbase.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popularpenguin.coinbase.repository.CoinTicker
import com.popularpenguin.coinbase.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: MainRepository
) : ViewModel() {

    private lateinit var job: Job
    private val _uiState = MutableStateFlow(TickerUIState.Success(null))
    val uiState: StateFlow<TickerUIState> = _uiState

    fun connect() {
        repository.connect()
        job = viewModelScope.launch {
            repository.getTicker().collect { ticker ->
                _uiState.value = TickerUIState.Success(ticker)
                delay(1000L) // rate limit for UI
            }
        }
    }

    fun disconnect() {
        job.cancel()
        repository.disconnect()
    }

    sealed class TickerUIState {
        data class Success(val ticker: CoinTicker?): TickerUIState()
        data class Error(val exception: Throwable): TickerUIState()
    }
}