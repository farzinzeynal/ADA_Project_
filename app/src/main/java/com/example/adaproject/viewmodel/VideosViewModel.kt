package com.example.adaproject.viewmodel

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.example.adaproject.api.VideoApi
import com.example.adaproject.helpers.CatchError
import com.example.adaproject.helpers.Constants
import com.example.adaproject.helpers.Constants.Companion.token
import com.example.adaproject.helpers.OkHttpHelper
import com.example.adaproject.helpers.RetrofitInstance
import com.example.adaproject.models.response.ValidationRes
import com.example.adaproject.models.response.VideoCanCreateRes
import com.example.adaproject.models.response.VideoResError

import com.example.adaproject.models.response.VideoResSuccess
import com.example.adaproject.repository.VideoRepositroy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.FieldPosition
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject
constructor(private val repository: VideoRepositroy, private val context: Context) : ViewModel()
{

    private val _response = MutableLiveData<VideoResSuccess>()
    private val _statusResponse = MutableLiveData<VideoCanCreateRes>()
    val videosLiveData: LiveData<VideoResSuccess> get() = _response
    val statusLiveData: LiveData<VideoCanCreateRes> get() = _statusResponse

    var uploadVideoLiveData: MutableLiveData<VideoResSuccess?> = MutableLiveData()
    fun getUploadVideoObserver(): MutableLiveData<VideoResSuccess?> {
        return uploadVideoLiveData
    }

    var updateVideoLiveData: MutableLiveData<VideoResSuccess?> = MutableLiveData()
    fun getupdateVideoObserver(): MutableLiveData<VideoResSuccess?> {
        return updateVideoLiveData
    }



    init {
        getVideos()
        getStatus()
    }

    private fun getVideos() = viewModelScope.launch{
        repository.getVideos().let { response ->

            if (response.isSuccessful) {
                Log.i("getVideos", "OK: ")
                _response.postValue(response.body())
            } else {
                //_response.postValue(null)
                Toast.makeText(context, "problem downloading video", Toast.LENGTH_SHORT).show()
                Log.d("response", "problem downloading video: ${response.code()}")
            }
        }
    }

    private fun getStatus() = viewModelScope.launch{
        repository.getStatus().let { response ->

            if (response.isSuccessful) {
                Log.i("getVideos", "OK: ")
                _statusResponse.postValue(response.body())
            } else {
                //_response.postValue(null)
                Toast.makeText(context, "problem  uploading video", Toast.LENGTH_SHORT).show()
                Log.d("response", "problem uploading video: ${response.code()}")
            }
        }
    }


    fun uplaodVideo(map: MutableMap<String, RequestBody>,token:String) {

        var service = token?.let { OkHttpHelper.getClient(it) }?.let {
            RetrofitInstance.createInstanceWithAuth(Constants.baseUrl, it).create(VideoApi::class.java)
        }
        val call: Call<VideoResSuccess>? = service?.upload(map)

        call?.enqueue(object : Callback<VideoResSuccess?> {
            override fun onResponse(call: Call<VideoResSuccess?>, response: Response<VideoResSuccess?>)
            {
                Log.d(ContentValues.TAG, "RequestUrl: ${response.raw().request().url()}")
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        uploadVideoLiveData.postValue(response.body())
                        Log.e("Response gotten is:", response.message())
                        Toast.makeText(context, "Video uploaded", Toast.LENGTH_LONG).show()
                    }
                } else {
                    uploadVideoLiveData.postValue(null)
                    Log.e("Response gotten is:", response.message())
                    Toast.makeText(context, "problem uploading image"+ response.message(), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<VideoResSuccess?>, t: Throwable) {
                uploadVideoLiveData.postValue(null)
                Log.e("Response gotten is", t.message!!, t)
                Toast.makeText(context, "problem uploading image {${t.message!!}}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateVideo(position:Long,map: MutableMap<String, RequestBody>,token:String) {

        var service = token?.let { OkHttpHelper.getClient(it) }?.let {
            RetrofitInstance.createInstanceWithAuth(Constants.baseUrl, it).create(VideoApi::class.java)
        }

        val call: Call<VideoResSuccess>? = service?.update(position ,map)
        call?.enqueue(object : Callback<VideoResSuccess?> {
            override fun onResponse(call: Call<VideoResSuccess?>, response: Response<VideoResSuccess?>)
            {
                Log.d(ContentValues.TAG, "RequestUrl: ${response.raw().request().url()}")
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        Log.e("Response gotten is:", response.message())
                        updateVideoLiveData.postValue(response.body())
                    }

                }
                else {
                    updateVideoLiveData.postValue(null)
                }
            }

            override fun onFailure(call: Call<VideoResSuccess?>, t: Throwable) {
                Log.e("Response gotten is", t.message!!, t)
                updateVideoLiveData.postValue(null)
            }
        })
    }






}