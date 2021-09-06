package com.example.adaproject.repository

import android.content.Context
import com.example.adaproject.api.VideoApi
import javax.inject.Inject

class VideoRepositroy
@Inject constructor(private val videoApi: VideoApi) {

    suspend fun getVideos() = videoApi.getVideos()

    suspend fun getStatus() = videoApi.getStatus()

}
