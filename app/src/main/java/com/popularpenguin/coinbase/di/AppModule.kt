package com.popularpenguin.coinbase.di

import com.popularpenguin.coinbase.repository.CoinbaseClient
import com.popularpenguin.coinbase.repository.MainRepository
import com.popularpenguin.coinbase.util.Constants
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Singleton
    @Provides
    fun provideCoinbaseClient(moshi: Moshi): CoinbaseClient {
        return CoinbaseClient(Constants.SOCKET_BASE_URL, moshi)
    }

    @Singleton
    @Provides
    fun provideMainRepository(client: CoinbaseClient): MainRepository {
        return MainRepository(client)
    }
}