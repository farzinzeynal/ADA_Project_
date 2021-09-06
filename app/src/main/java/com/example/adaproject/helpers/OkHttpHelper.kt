package com.example.adaproject.helpers

import okhttp3.OkHttpClient

class OkHttpHelper {
    companion object{
        fun getClient(token:String):OkHttpClient{
            var client = OkHttpClient.Builder().addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer "+token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build()
            return client
        }
    }
}