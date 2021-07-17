package edu.bupt.jetdeepl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.bupt.jetdeepl.model.DeeplRepo
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun provideDeeplRepo(okHttpClient: OkHttpClient): DeeplRepo {
        return DeeplRepo(okHttpClient)
    }
}