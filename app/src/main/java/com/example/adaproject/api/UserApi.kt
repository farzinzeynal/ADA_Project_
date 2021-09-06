package com.example.adaproject.api

import com.example.adaproject.models.request.UserReq
import com.example.adaproject.models.response.UserErrorRes
import com.example.adaproject.models.response.UserInfoRes
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @POST("/user")
    fun fillInfo(@Body user:UserReq) : Call<UserErrorRes>

    @GET("/user")
    suspend fun getInfo() : Response<UserInfoRes>

}