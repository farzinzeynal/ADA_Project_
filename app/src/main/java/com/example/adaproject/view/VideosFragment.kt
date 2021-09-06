package com.example.adaproject.view

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adaproject.MainActivity
import com.example.adaproject.adapters.VideoItemAdapter
import com.example.adaproject.helpers.*
import com.example.adaproject.api.VideoApi
import com.example.adaproject.models.response.VideoResError
import com.example.adaproject.models.response.VideoResSuccess
import com.example.adaproject.databinding.FragmentVideosBinding
import com.example.adaproject.models.response.Video
import com.example.adaproject.models.response.VideoCanCreateRes
import com.example.adaproject.viewmodel.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.prefs.Prefs
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.HashMap

@AndroidEntryPoint
class VideosFragment : Fragment(),VideoItemAdapter.IVideoItemListener {

      var videolist = arrayListOf<Video>()
      private lateinit var binding: FragmentVideosBinding
      private lateinit var token:String
      private lateinit var videoPath:String
      private lateinit var linearLayoutManager: LinearLayoutManager
      private lateinit var adapter: VideoItemAdapter
      private lateinit var updateResultLauncher: ActivityResultLauncher<Intent>
      private lateinit var resultLauncher: ActivityResultLauncher<Intent>
      private var _position:Long = -1
      private var _process:Int = -1

      private val viewModel: VideosViewModel by viewModels<VideosViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentVideosBinding.inflate(inflater, container, false)
        var view = binding.root

        token = context?.let { Prefs.with(it).read("token",null) }.toString()
        Log.e(TAG, "token: $token")

        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

         resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.d(ContentValues.TAG, "onActivityResult: ${data?.dataString}")
                if (data != null) {
                    Log.d(ContentValues.TAG, "RealPath: ${getPath(data.data)}")
                    videoPath = getPath(data.data)!!
                    checkCanCreateThenPost()
                }

            }
        }

         updateResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.d(ContentValues.TAG, "onActivityResult: ${data?.dataString}")
                if (data != null) {
                    Log.d(ContentValues.TAG, "RealPath: ${getPath(data.data)}")
                    videoPath = getPath(data.data)!!
                    checkCanReCreateThenPost()
                }
            }
        }

        binding.capture.setOnClickListener {
            changeLayout(Constants.CAPTURE)
        }

        binding.reCapture.setOnClickListener {
            changeLayout(Constants.CAPTURE)
        }

        ButtonHandler(binding.ready).disable()

        var checkedListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(checkAllPrep())
                ButtonHandler(binding.ready).enable()
            else
                ButtonHandler(binding.ready).disable()
        }

        binding.prep1.setOnCheckedChangeListener (checkedListener)
        binding.prep2.setOnCheckedChangeListener (checkedListener)
        binding.prep3.setOnCheckedChangeListener (checkedListener)
        binding.exit.setOnClickListener{
            changeLayout(-1)
        }

        binding.ready.setOnClickListener {
            changeLayout(_process)
            videoPrep(_process)
        }

        openVideoList()

        return view

    }
    
    fun checkAllPrep():Boolean
    {
        if(binding.prep1.isChecked && binding.prep2.isChecked && binding.prep3.isChecked)
            return true
        else
            return false
    }

    fun resetCheckbox()
    {
        binding.prep1.isChecked = false
        binding.prep2.isChecked = false
        binding.prep3.isChecked = false
    }

    fun checkCanCreateThenPost()
    {
        viewModel.statusLiveData.observe(requireActivity(),
            { result ->
                uploadFile()
        })
    }

    fun checkCanReCreateThenPost()
    {
        viewModel.statusLiveData.observe(requireActivity(),
            { result ->
                updateVideo(_position)
            })
    }


    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? =
            uri?.let { MainActivity._getContentResolver().query(it, projection, null, null, null) }
        return if (cursor != null) {
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }


    private fun uploadFile() {

        val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }
        if (loading != null) { loading.startLoading() }
        // Map is used to multipart the file using okhttp3.RequestBod
        val map: MutableMap<String, RequestBody> = HashMap()
        val file: File = File(videoPath)
        // Parsing any Media type file
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        map["file\"; filename=\"" + file.name + "\""] = requestBody

        viewModel.uplaodVideo(map,token)
        viewModel.getUploadVideoObserver().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.emptyLayout.isVisible=false
                openVideoList()
                if (loading != null) { loading.isDismiss() }

            } else {
                if (loading != null) { loading.isDismiss() }
             }
           })
    }



    private fun openVideoList()
    {
        binding.recyclerVideosLayout.isVisible = true

        val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }
        if (loading != null) { loading.startLoading() }

           viewModel.videosLiveData.observe(requireActivity(),
              { result ->
                  Log.i("resultSize", "VideoList: {${result.videos.size}}")
                  videolist = result.videos as ArrayList<Video>
                  if (loading != null) { loading.isDismiss() }

                  if(videolist.size==0) {
                      binding.emptyLayout.isVisible=true
                      binding.recyclerVideosLayout.isVisible = false
                  }
                  else {
                      binding.emptyLayout.isVisible=false
                      binding.recyclerVideosLayout.isVisible = true
                  }
                  adapter = VideoItemAdapter(videolist, this@VideosFragment)
                  binding.recyclerView.adapter = adapter
            })
    }


    fun updateVideo(position: Long)
    {
        val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }
        if (loading != null) { loading.startLoading() }

        // Map is used to multipart the file using okhttp3.RequestBody
        val map: MutableMap<String, RequestBody> = HashMap()
        val file: File = File(videoPath)
        // Parsing any Media type file
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        map["file\"; filename=\"" + file.name + "\""] = requestBody

        viewModel.updateVideo(position,map,token)
        viewModel.getupdateVideoObserver().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.emptyLayout.isVisible=false
                openVideoList()
                Toast.makeText(context, "Video updated", Toast.LENGTH_LONG).show()
                if (loading != null) { loading.isDismiss() }

            } else {

                if (loading != null) { loading.isDismiss() }
                Toast.makeText(context, "problem updating video", Toast.LENGTH_SHORT).show()
            }
        })

    }


    override fun onRecapture(position: Int) {
        _position =  videolist[position].id.toLong()
        changeLayout(Constants.UPDATE)
    }

    fun changeLayout(process:Int)
    {
        if(binding.videoPrepLayout.visibility == View.GONE) {
            binding.videoPrepLayout.visibility = View.VISIBLE
            binding.videosLayout.visibility = View.GONE
        }
        else
        {
            binding.videoPrepLayout.visibility = View.GONE
            binding.videosLayout.visibility = View.VISIBLE
        }
        resetCheckbox()
        _process = process
    }

    fun videoPrep(process:Int)
    {
        if(process == Constants.CAPTURE) {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            resultLauncher.launch(intent)
        }
        else {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            updateResultLauncher.launch(intent)
        }
    }


}


