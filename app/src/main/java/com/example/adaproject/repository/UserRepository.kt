package com.example.adaproject.repository

import android.content.Context
import com.example.adaproject.api.UserApi
import com.example.adaproject.api.VideoApi
import javax.inject.Inject

class UserRepository
@Inject constructor(private val userApi: UserApi) {

    suspend fun getUserInfo() = userApi.getInfo()

}
