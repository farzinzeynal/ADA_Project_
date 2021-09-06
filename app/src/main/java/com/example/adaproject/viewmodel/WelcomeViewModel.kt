package com.example.adaproject.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.example.adaproject.MainActivity
import com.example.adaproject.api.ValidationApi
import com.example.adaproject.view.ValidationFragment
import com.example.adaproject.helpers.Constants
import com.example.adaproject.helpers.RetrofitInstance
import com.example.adaproject.models.request.PhoneNumberReq
import com.example.adaproject.models.response.PhoneNumberRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WelcomeViewModel : ViewModel()
{

    var liveData: MutableLiveData<PhoneNumberRes?>

    init {
        liveData = MutableLiveData()
    }

    fun getObserver(): MutableLiveData<PhoneNumberRes?> {
        return liveData
    }



     fun sendNumber(_phoneNumberReq: PhoneNumberReq)
     {

         var validationApi = RetrofitInstance.createInstance(Constants.baseUrl).create(ValidationApi::class.java)
         var call = validationApi.sendNum(_phoneNumberReq)

         call.enqueue(object : Callback<PhoneNumberRes> {
             override fun onResponse(call: Call<PhoneNumberRes>, response: Response<PhoneNumberRes>) {

                 Log.d("Response", response.body().toString())
                 if(response.isSuccessful && response !=null) {
                     liveData.postValue(response.body())
                 }
                 else
                     liveData.postValue(null)
             }

             override fun onFailure(call: Call<PhoneNumberRes>, t: Throwable) {
                     liveData.postValue(null)
             }
         })
    }


}