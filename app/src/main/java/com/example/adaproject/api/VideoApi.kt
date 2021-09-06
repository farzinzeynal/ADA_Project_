package com.example.adaproject.api

import com.example.adaproject.models.response.VideoCanCreateRes
import com.example.adaproject.models.response.VideoDemandRes
import com.example.adaproject.models.response.VideoResSuccess
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface VideoApi {

    @Multipart
    @POST("/video")
    fun upload(
        @PartMap map: MutableMap<String, RequestBody>
    ): Call<VideoResSuccess>

    @GET("/video/list")
    suspend fun getVideos(): Response<VideoResSuccess>

    @GET("/video/can-create")
    suspend fun getStatus() : Response<VideoCanCreateRes>

    @GET("/video/demand/report")
    fun demandStatus() : Call<VideoDemandRes>

    @Multipart
    @POST("/video/{videoId}")
    fun update(
        @Path("videoId") id:Long,
        @PartMap map: MutableMap<String, RequestBody>
    ): Call<VideoResSuccess>

}