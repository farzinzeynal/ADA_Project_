package com.example.adaproject.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adaproject.databinding.FragmentValidationBinding
import com.example.adaproject.api.ValidationApi
import com.example.adaproject.models.request.ValidationReq
import com.example.adaproject.models.response.ValidationRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import androidx.preference.PreferenceManager
import com.example.adaproject.MainActivity
import com.example.adaproject.helpers.*
import com.example.adaproject.models.response.PhoneNumberRes
import com.example.adaproject.viewmodel.ValidationViewModel
import com.example.adaproject.viewmodel.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.prefs.Prefs

@AndroidEntryPoint
class ValidationFragment : Fragment() {

    private lateinit var binding: FragmentValidationBinding
    private lateinit var countDownTimer: CountDownTimer
    private val START_TIME_IN_MILLIS: Long = 60000
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    private var mTimerRunning = false
    lateinit var preferenceManager: PreferenceManager
    val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }

    lateinit var viewModel: ValidationViewModel

    private var userId:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentValidationBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel()
        startTimer()

        userId = arguments?.getInt("id")
        Log.d("Id", arguments?.getInt("id").toString())

        ButtonHandler(binding.nextBtn).disable()
        binding.validationCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.validationCode.text.toString()!="")
                    ButtonHandler(binding.nextBtn).enable()
                else
                    ButtonHandler(binding.nextBtn).disable()
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged: changed")
            }
        })

        binding.nextBtn.setOnClickListener {
            val validationReq = ValidationReq(binding.validationCode.text.toString())
            Log.d(TAG, binding.validationCode.text.toString())
            userId?.let { it1 -> viewModel.getToken(validationReq, it1) }
            if (loading != null) { loading.startLoading() }
        }


        return view
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(activity as FragmentActivity).get(ValidationViewModel::class.java)
        viewModel.getObserver().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if(it == null) {
                Toast.makeText(context, "Please enter valid number!", Toast.LENGTH_LONG).show()
                if (loading != null) { loading.isDismiss() }
            }
            else {
                if (loading != null) { loading.isDismiss() }
                val token = it.token
                context?.let { it1 -> Prefs.with(it1).write("token", token) }
                if (token != "" && token!=null) {
                    Constants.token=token
                    countDownTimer.cancel()
                    var userInfromation = UserInfromation()
                    MainActivity.gotoFragm(userInfromation)
                }
            }
        })
    }




    private fun startTimer() {
        countDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                val welcomeFragment = WelcomeFragment()
                MainActivity.gotoFragm(welcomeFragment)
            }
        }.start()
    }


    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted: String = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.countDown.text = timeLeftFormatted
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ValidationFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}