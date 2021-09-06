package com.example.adaproject.api

import com.example.adaproject.models.request.PhoneNumberReq
import com.example.adaproject.models.request.ValidationReq
import com.example.adaproject.models.response.PhoneNumberRes
import com.example.adaproject.models.response.ValidationRes
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ValidationApi {
    @POST("/auth")
    fun sendNum(@Body phoneNumberReq: PhoneNumberReq):Call<PhoneNumberRes>

    @POST("validate")
    fun getToken(@Body validationReq: ValidationReq):Call<ValidationRes>
}