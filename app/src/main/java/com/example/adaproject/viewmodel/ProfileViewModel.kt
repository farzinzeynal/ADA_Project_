package com.example.adaproject.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adaproject.api.VideoApi
import com.example.adaproject.helpers.ButtonHandler
import com.example.adaproject.models.response.*
import com.example.adaproject.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel@Inject
constructor(private val repository: UserRepository, private val context: Context)  : ViewModel()
{
    private val userInfoResponse = MutableLiveData<UserInfoRes>()
    val userInfoLiveData: LiveData<UserInfoRes> get() = userInfoResponse

    init {
        getUserInfo()
    }

    var demandStatusLiveData: MutableLiveData<VideoDemandRes?> = MutableLiveData()
    fun getDemandStatusObserver(): MutableLiveData<VideoDemandRes?> {
        return demandStatusLiveData
    }


    fun demandStatus(service: VideoApi)
   {
       var call = service.demandStatus()
       call.enqueue(object: Callback<VideoDemandRes> {
           override fun onResponse(call: Call<VideoDemandRes>, response: Response<VideoDemandRes>) {
               if(response.isSuccessful) {
                   demandStatusLiveData.postValue(response.body())
               }
               else {
                   demandStatusLiveData.postValue(null)
               }
           }

           override fun onFailure(call: Call<VideoDemandRes>, t: Throwable) {
               Log.e("Demand gotten is", t.message!!, t)
               demandStatusLiveData.postValue(null)
           }

       })
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