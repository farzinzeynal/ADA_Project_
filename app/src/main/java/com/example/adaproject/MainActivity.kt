package com.example.adaproject

import android.app.ActionBar
import android.content.ContentResolver
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.adaproject.databinding.ActivityMainBinding
import com.example.adaproject.view.*
import com.example.adaproject.helpers.Constants
import com.example.adaproject.helpers.OkHttpHelper
import com.example.adaproject.helpers.RetrofitInstance
import com.example.adaproject.api.UserApi
import com.example.adaproject.models.response.UserInfoRes
import com.example.adaproject.viewmodel.MainViewModel
import com.example.adaproject.viewmodel.ValidationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.prefs.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var binding: ActivityMainBinding
private lateinit var toolbar: Toolbar
private lateinit var actionBar1: ActionBar
lateinit var botnav:BottomNavigationView
lateinit var supportFragManager: FragmentManager
private lateinit var _layoutInflater:LayoutInflater
private lateinit var writePath:String
private lateinit var resolver:ContentResolver
private lateinit var context: Context

private val validationFragment: ValidationFragment = ValidationFragment()
private val welcomeFragment: WelcomeFragment = WelcomeFragment()
private val userInfromation: UserInfromation = UserInfromation()
private val videosFragment: VideosFragment = VideosFragment()
private val profileFragment: ProfileFragment = ProfileFragment()
private val testFragment = TestFragment()




@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _layoutInflater = layoutInflater
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resolver = contentResolver
        context = this

        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf<String>(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )

        val permission =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )

        }

        writePath = this.getExternalFilesDir("content://media/external/video/media").toString()
        botnav = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        supportFragManager = supportFragmentManager

        var token = Prefs.with(this).read("token")
        if(token== "")
            gotoFragm(welcomeFragment)
        else
        {
            Constants.token=token
            getUserInfo()
        }

        binding.bottomNavBar.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.videos -> gotoFragm(videosFragment)
                R.id.profile -> gotoFragm(profileFragment)
            }
            true
        }
        binding.bottomNavBar.visibility = View.GONE
    }

    private fun getUserInfo() {
        viewModel.userInfoLiveData.observe(this,
            { result ->

                if(result!=null)
                {
                    Log.i("result", "${result.toString()}}")
                    if (result.privacyContract && result.reportContract) {
                        gotoFragm(videosFragment)
                        botnav.visibility = View.VISIBLE
                    }
                    else
                        gotoFragm(userInfromation)
                }
                else{
                    gotoFragm(welcomeFragment)
                }
            })
    }



    companion object{
        fun BotNav():BottomNavigationView
        {
            return botnav
        }
        fun gotoFragm(frag: Fragment){
            supportFragManager.beginTransaction().apply {
                replace(binding.straterFrame.id, frag)
                commit()
            }
        }
        fun getInflater():LayoutInflater
        {
            return _layoutInflater
        }
        fun fileDir():String{
            return writePath
        }
        fun _getContentResolver():ContentResolver{
            return resolver
        }
        fun getContext():Context
        {
            return context
        }
    }


}