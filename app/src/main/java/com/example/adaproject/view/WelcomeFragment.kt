package com.example.adaproject.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.adaproject.MainActivity
import com.example.adaproject.databinding.FragmentWelcomeBinding
import com.example.adaproject.helpers.ButtonHandler
import com.example.adaproject.helpers.LoadingDialog
import com.example.adaproject.models.request.PhoneNumberReq
import com.example.adaproject.models.response.PhoneNumberRes
import com.example.adaproject.viewmodel.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class WelcomeFragment : Fragment(){


    private lateinit var viewModel: WelcomeViewModel

    val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }

    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val view = binding.root

        initViewModel();



        ButtonHandler(binding.nextBtn).disable()
        binding.number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: textChanged")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.number.text.toString() != "")
                    ButtonHandler(binding.nextBtn).enable()
                else
                    ButtonHandler(binding.nextBtn).disable()
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "beforeTextChanged: textChanged")
            }
        })

        binding.nextBtn.setOnClickListener{
            var phoneNumberReq: PhoneNumberReq = PhoneNumberReq("0090"+binding.number.text.toString())
            viewModel.sendNumber(phoneNumberReq)
        }


        return view
    }

    private fun initViewModel() {

        viewModel = ViewModelProvider(activity as FragmentActivity).get(WelcomeViewModel::class.java)
        viewModel.getObserver().observe(viewLifecycleOwner, Observer <PhoneNumberRes?>{

            if(it == null) {
                Toast.makeText(context, "Please enter valid number!", Toast.LENGTH_LONG).show()
                if (loading != null) { loading.isDismiss() }
            }
            else {
                if (loading != null) { loading.isDismiss() }
                var validationFragment: ValidationFragment = ValidationFragment()
                var bundle: Bundle = Bundle()
                bundle.putInt("id", it.id)
                validationFragment.arguments = bundle
                MainActivity.gotoFragm(validationFragment)
            }
        })
    }


}