package com.example.adaproject.viewmodel

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adaproject.MainActivity
import com.example.adaproject.api.UserApi
import com.example.adaproject.helpers.CatchError
import com.example.adaproject.helpers.Constants
import com.example.adaproject.helpers.OkHttpHelper
import com.example.adaproject.helpers.RetrofitInstance
import com.example.adaproject.models.request.UserReq
import com.example.adaproject.models.response.*
import com.example.adaproject.repository.UserRepository
import com.example.adaproject.repository.VideoRepositroy
import com.example.adaproject.view.VideosFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import es.dmoral.prefs.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserInfromationViewModel
@Inject
constructor(private val repository: UserRepository, private val context: Context)  : ViewModel()
{


    var liveData: MutableLiveData<UserErrorRes?>

    init {
        liveData = MutableLiveData()
    }

    fun getObserver(): MutableLiveData<UserErrorRes?> {
        return liveData
    }

    fun sendUserInfo(userReq: UserReq, context:Context )
    {
        val client = context?.let { it1 -> Prefs.with(it1).read("token", null) }?.let { it2 ->
            OkHttpHelper.getClient(it2)
        }
        if(client == null)
            Log.e(ContentValues.TAG, "onCreateView: client is null", client)
        var service = client?.let { it1 -> RetrofitInstance.createInstanceWithAuth(
            Constants.baseUrl,
            it1
        ).create(UserApi::class.java) }
        var call = service?.fillInfo(userReq)
        if (call != null) {
            call.enqueue(object : Callback<UserErrorRes> {
                override fun onResponse(
                    call: Call<UserErrorRes>, response: Response<UserErrorRes>) {
                    if(response.code()==200) {
                       liveData.postValue(response.body())
                    }
                    else
                    liveData.postValue(null)
                    CatchError.catchServerError<UserErrorRes>(response.errorBody())
                }

                override fun onFailure(call: Call<UserErrorRes>, t: Throwable) {
                    liveData.postValue(null)
                    Log.d(ContentValues.TAG, "onFailure: $t")
                }
            })
        }
        else
            Log.e(ContentValues.TAG, "onCreateView: call is null!", call)
    }



}