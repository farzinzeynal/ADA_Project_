package com.example.adaproject.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.adaproject.*
import com.example.adaproject.databinding.FragmentProfileBinding
import com.example.adaproject.helpers.*
import com.example.adaproject.api.VideoApi
import com.example.adaproject.models.response.UserInfoRes
import com.example.adaproject.models.response.VideoDemandRes
import com.example.adaproject.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.prefs.Prefs
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    var userInfoRes: UserInfoRes? = null
    lateinit var token:String
    lateinit var client: OkHttpClient
    private val viewModel: ProfileViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        var view = binding.root

        token = context?.let { Prefs.with(it).read("token") }.toString()
        client = token?.let { OkHttpHelper.getClient(it) }


        getInfo()
        binding.demandBtn.setOnClickListener {
            Toast.makeText(context,"test",Toast.LENGTH_SHORT).show()
        }
        ButtonHandler(binding.demandBtn).disable()

        return view
    }

    fun demandButton()
    {
        var service = client?.let { RetrofitInstance.createInstanceWithAuth(Constants.baseUrl, it).create(VideoApi::class.java) }
        viewModel.demandStatus(service)
        viewModel.getDemandStatusObserver().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it == null) {
                Toast.makeText(
                    context,
                    "Talep statüsü alımında problem" ,
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                if(it.status==true)
                    ButtonHandler(binding.demandBtn).enable()
            }
        })
    }

    fun getInfo()
    {
        demandButton()

        val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }
        if (loading != null) { loading.startLoading() }

        viewModel.userInfoLiveData.observe(requireActivity(),
            { result ->
                Log.i("result", "${result.toString()}}")
                if(result!=null)
                {
                    if (loading != null) { loading.isDismiss() }
                    binding.user = result

                  //  binding.birthDate.text = result.child.realBirthDate
                  //  binding.birthWeight.text = result.child.grams.toString()
                  //  binding.email.text = result.email
                  //  binding.expectedDate.text = result.child.estimatedBirthDate
                  //  binding.fullName.text =result.child.name
                  //  binding.sexuality.text = result.child.sexuality
                }
                else{
                    if (loading != null) { loading.isDismiss() }
                    Toast.makeText(
                        context,
                        "Something went wrong! Code: }",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

}