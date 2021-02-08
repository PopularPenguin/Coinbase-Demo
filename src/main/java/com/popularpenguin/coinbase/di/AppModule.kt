package com.popularpenguin.coinbase.di

import com.popularpenguin.coinbase.repository.CoinbaseClient
import com.popularpenguin.coinbase.repository.MainRepository
import com.popularpenguin.coinbase.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideCoinbaseClient(): CoinbaseClient {
        return CoinbaseClient(Constants.SOCKET_BASE_URL)
    }

    @Singleton
    @Provides
    fun provideMainRepository(client: CoinbaseClient): MainRepository {
        return MainRepository(client)
    }
}