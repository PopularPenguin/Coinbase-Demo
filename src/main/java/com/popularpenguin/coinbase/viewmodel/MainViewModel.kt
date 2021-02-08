package com.popularpenguin.coinbase.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.popularpenguin.coinbase.repository.CoinTicker
import com.popularpenguin.coinbase.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: MainRepository
) : ViewModel() {

    fun onResume() {
        repository.connect()
    }

    fun onPause() {
        repository.disconnect()
    }

    fun getTicker(): Flow<CoinTicker?> {
        return repository.getTicker()
    }
}