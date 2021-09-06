package com.example.adaproject.helpers

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {
    companion object{
           fun createInstance(baseUrl:String):Retrofit {
            var retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit
        }

            fun createInstanceWithAuth(baseUrl:String, client: OkHttpClient):Retrofit{
                var retrofit:Retrofit = Retrofit.Builder()
                        .client(client)
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                return retrofit
        }
    }

}