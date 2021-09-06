package com.example.adaproject.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.adaproject.MainActivity
import com.example.adaproject.api.ValidationApi
import com.example.adaproject.helpers.CatchError
import com.example.adaproject.helpers.Constants
import com.example.adaproject.helpers.RetrofitInstance
import com.example.adaproject.models.request.ValidationReq
import com.example.adaproject.models.response.PhoneNumberRes
import com.example.adaproject.models.response.ValidationRes
import com.example.adaproject.view.UserInfromation
import es.dmoral.prefs.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ValidationViewModel : ViewModel()
{

    var liveData: MutableLiveData<ValidationRes?>

    init {
        liveData = MutableLiveData()
    }

    fun getObserver(): MutableLiveData<ValidationRes?> {
        return liveData
    }



     fun getToken( validationReq: ValidationReq,userId:Int)
     {

         var validationApi = RetrofitInstance.createInstance(Constants.baseUrlAuth+userId+'/').create(ValidationApi::class.java)
         var call = validationApi.getToken(validationReq)

         call.enqueue(object : Callback<ValidationRes> {
             override fun onResponse(call: Call<ValidationRes>, response: Response<ValidationRes>) {
                 if (response.isSuccessful) {
                     liveData.postValue(response.body())
                 }
                 else
                     liveData.postValue(null)
                     CatchError.catchServerError<ValidationRes>(response.errorBody())
             }

             override fun onFailure(call: Call<ValidationRes>, t: Throwable) {
                 Log.d("Error", t.message.toString())
                 liveData.postValue(null)
             }
         })
    }




}