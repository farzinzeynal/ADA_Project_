package com.example.adaproject.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.adaproject.MainActivity
import com.example.adaproject.R
import com.example.adaproject.databinding.FragmentUserInfromationBinding
import com.example.adaproject.helpers.*
import com.example.adaproject.api.UserApi
import com.example.adaproject.models.request.Child
import com.example.adaproject.models.request.UserReq
import com.example.adaproject.models.response.UserErrorRes
import com.example.adaproject.viewmodel.UserInfromationViewModel
import com.example.adaproject.viewmodel.ValidationViewModel
import es.dmoral.prefs.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class UserInfromation : Fragment() {

    private lateinit var binding: FragmentUserInfromationBinding
    private lateinit var child: Child
    private lateinit var userReq: UserReq
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var viewModel: UserInfromationViewModel
    val loading  = context?.let { LoadingDialog(MainActivity.getInflater(), it) }


    private  var expectedDate:String? = null
    private  var realDate:String? = null
    private var sexuality:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       binding = FragmentUserInfromationBinding.inflate(inflater, container, false)
        var view = binding.root

        initViewModel();

        ButtonHandler(binding.nextBtn).disable()
        var checkedListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(checkAllCheckBoxes())
                ButtonHandler(binding.nextBtn).enable()
            else
                ButtonHandler(binding.nextBtn).disable()
        }
        binding.privacyPolicy.setOnCheckedChangeListener(checkedListener)
        binding.reportPolicy.setOnCheckedChangeListener(checkedListener)

        binding.expectedDate.setOnClickListener{
            datePickerDialog(binding.expectedDate) }

        binding.birthDate.setOnClickListener {
            datePickerDialog(binding.birthDate) }

        binding.nextBtn.setOnClickListener {
            if(
                !isEmpty(binding.fullName) &&
                !isEmpty(binding.birthWeight) &&
                !isEmpty(binding.email) &&
                binding.radGr.checkedRadioButtonId != null &&
                binding.expectedDate.text != "" &&
                binding.birthDate.text != ""
            )
            {
                var rb = view.findViewById<RadioButton>(binding.radGr.checkedRadioButtonId)
                if (rb.text.toString() == Constants.feMale)
                    sexuality = "FEMALE"
                else
                    sexuality = "MALE"
                
                child = Child(
                    binding.refDr.text.toString(),
                    formatDate(binding.expectedDate.text.toString()),
                    Integer.parseInt(binding.birthWeight.text.toString()),
                    binding.fullName.text.toString(),
                    formatDate(binding.birthDate.text.toString()),
                    sexuality.toString()
                )

                if (binding.privacyPolicy.isChecked && binding.reportPolicy.isChecked)
                {
                    Log.d(TAG, "onCreateView: send informations")
                    userReq = UserReq(child, binding.email.text.toString(), true, true)
                    showDialogForInfoPost()
                }
                else{
                    Toast.makeText(
                        context,
                        "Lütfen şartları doğrulayın!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else {
                Toast.makeText(
                    context,
                    "Lütfen girdiğiniz verilerin doğrulunu tekrar kontrol edin!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return view
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity as FragmentActivity).get(UserInfromationViewModel::class.java)
        viewModel.getObserver().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if(it == null) {
                if (loading != null) { loading.isDismiss() }
                Toast.makeText(context, "Error while sending infromation", Toast.LENGTH_LONG).show()
            }
            else {
                if (loading != null) { loading.isDismiss() }
                var videosFragment = VideosFragment()
                MainActivity.BotNav().visibility = View.VISIBLE
                MainActivity.gotoFragm(videosFragment)
            }
        })
    }

    fun checkAllCheckBoxes():Boolean
    {
        if(binding.reportPolicy.isChecked && binding.privacyPolicy.isChecked)
            return true
        else
            return false
    }

    fun sendUserInfo()
    {
        context?.let { viewModel.sendUserInfo(userReq, it) }
        if (loading != null) { loading.startLoading() }
    }

    fun showDialogForInfoPost() {
        val dialog = context?.let { Dialog(it) }
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        if (dialog != null) {
            dialog.setCancelable(false)
        }
        if (dialog != null) {
            dialog.setContentView(R.layout.user_info_val_dialog_item)
        }
        val yesBtn = dialog?.findViewById(R.id.validateBtn) as Button
        val noBtn = dialog.findViewById(R.id.returnBtn) as TextView
        yesBtn.setOnClickListener {
            sendUserInfo()
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }



    private fun isEmpty(etText: EditText): Boolean {
        return etText.text.toString().trim { it <= ' ' }.isEmpty()
    }

    fun formatDate(dateString:String):String{
        val fmt = SimpleDateFormat("d-M-yyyy")
        val date: Date = fmt.parse(dateString)
        val fmtOut = SimpleDateFormat("yyyy-MM-dd")
       return fmtOut.format(date)
    }

    fun datePickerDialog(textView: TextView)
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd =
            context?.let {
                DatePickerDialog(it, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    textView.setText(dayOfMonth.toString() + "-" + (monthOfYear+1) + "-" + year)
                }, year, month, day)
            }
        if (dpd != null) {
            dpd.datePicker.maxDate = Date().time
        }

        if (dpd != null) {
            dpd.show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UserInfromation().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}