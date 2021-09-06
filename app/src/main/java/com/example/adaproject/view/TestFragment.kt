package com.example.adaproject.view

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.adaproject.MainActivity
import com.example.adaproject.databinding.FragmentTestBinding
import com.example.adaproject.helpers.*
import com.example.adaproject.api.VideoApi
import com.example.adaproject.models.response.VideoResError
import com.example.adaproject.models.response.VideoResSuccess
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    var token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDkwNzc3ODg4ODAwMix0cnVlIiwiaWF0IjoxNjI3NTU0NDAyfQ.JQ2tGSU3gY-xjusE_SOoYEATjgGZcOBsdOTfBs3sAVlwW-ZTx622F0ueVF0wQYl-73M3ZoAA4WjRznDXvILJfg"
    private lateinit var videoPath:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTestBinding.inflate(inflater, container, false)
        var view = binding.root

        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                binding.videoView.setVideoURI(data?.data)
                binding.videoView.start()
                Log.d(TAG, "onActivityResult: ${data?.dataString}")
                if (data != null) {
                    Log.d(TAG, "RealPath: ${getPath(data.data)}")
                    videoPath = getPath(data.data)!!
                    uploadFile()
                }
                activateLicenses()
            }
        }

        binding.nextBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            resultLauncher.launch(intent)
        }

        return view
    }
    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? =
            uri?.let { MainActivity._getContentResolver().query(it, projection, null, null, null) }
        return if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }

    fun activateLicenses() {

        val directory = File(MainActivity.fileDir())
        if (!directory.exists()) Log.d(
            "DEBUG_TAG",
            if (directory.mkdirs()) "Directory has been created" else "Directory not created"
        ) else Log.d("DEBUG_TAG", "Directory exists")


        val files: Array<File> = directory.listFiles()
        if (files != null) {
            Log.d("Files", "Size: " + files.size)
            for (file in files) {
                Log.d("Files", "FileName:" + file.getName())
            }
        }
    }

    private fun uploadFile() {

        val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }
        if (loading != null) {
            loading.startLoading()
        }

            // Map is used to multipart the file using okhttp3.RequestBody
            val map: MutableMap<String, RequestBody> = HashMap()
            val file: File = File(videoPath)
            // Parsing any Media type file
            val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
            map["file\"; filename=\"" + file.name + "\""] = requestBody
        var service = RetrofitInstance.createInstanceWithAuth(Constants.baseUrl,OkHttpHelper.getClient(token)).create(VideoApi::class.java)
            val call: Call<VideoResSuccess> = service.upload(map)
            call.enqueue(object : Callback<VideoResSuccess?> {
                override fun onResponse(
                    call: Call<VideoResSuccess?>,
                    response: Response<VideoResSuccess?>
                ) {
                    if (loading != null) {
                        loading.isDismiss()
                    }
                    if (response.isSuccessful()) {


                        if (response.body() != null) {

                            val serverResponse: VideoResSuccess? = response.body()
                            if (serverResponse != null) {
                                Log.d(TAG, "onResponse: $serverResponse")
                            }
                        }
                    } else {
                        CatchError.catchServerError<VideoResError>(response.errorBody())
                        Toast.makeText(
                            context,
                            "problem uploading image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<VideoResSuccess?>, t: Throwable) {

                    if (loading != null) {
                        loading.isDismiss()
                    }

                    Log.e("Response gotten is", t.message!!, t)
                    Toast.makeText(
                        context,
                        "problem uploading image " + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }

}