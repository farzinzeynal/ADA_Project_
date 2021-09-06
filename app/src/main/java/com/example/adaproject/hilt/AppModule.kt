package com.example.adaproject.hilt

import android.app.Application
import android.content.Context
import com.example.adaproject.api.UserApi
import com.example.adaproject.helpers.Constants
import com.example.adaproject.api.VideoApi
import com.example.adaproject.helpers.OkHttpHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule
{
    @Provides
    @Singleton
    fun provideVideoRetrofitInstance(): VideoApi =
        Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .client(OkHttpHelper.getClient(Constants.token))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(VideoApi::class.java)

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext


    @Provides
    @Singleton
    fun provideUserRetrofitInstance(): UserApi =
        Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .client(OkHttpHelper.getClient(Constants.token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)

}