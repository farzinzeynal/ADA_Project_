package com.example.adaproject.viewmodel

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adaproject.*
import com.example.adaproject.api.UserApi
import com.example.adaproject.helpers.Constants
import com.example.adaproject.helpers.OkHttpHelper
import com.example.adaproject.helpers.RetrofitInstance
import com.example.adaproject.models.response.UserInfoRes
import com.example.adaproject.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(private val repository: UserRepository, private val context: Context)   : ViewModel()
{
    private val userInfoResponse = MutableLiveData<UserInfoRes>()
    val userInfoLiveData: LiveData<UserInfoRes> get() = userInfoResponse

    init {
        getUserInfo()
    }

    private fun getUserInfo() = viewModelScope.launch{
        repository.getUserInfo().let { response ->

            if (response.isSuccessful) {
                Log.i("getUserInfo", response.body().toString())
                userInfoResponse.postValue(response.body())
            } else {
                //_response.postValue(null)
                Toast.makeText(context, "problem getting info", Toast.LENGTH_SHORT).show()
                Log.d("response", "problem getting info: ${response.code()}")
            }
        }
    }
}