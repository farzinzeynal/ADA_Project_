package com.example.adaproject.helpers

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.example.adaproject.MainActivity
import okhttp3.ResponseBody
import org.json.JSONObject

class CatchError {
    companion object{
        fun <T> catchServerError(error: ResponseBody?) {
            try {
                val jObjError =
                    JSONObject(error!!.string())
                val messageServer = jObjError.getString("error")
                val errorServer = jObjError.getString("message")
                Log.d(ContentValues.TAG, "messageServer $messageServer + errorServer $errorServer")
                Toast.makeText(MainActivity.getContext(),"ServerMessage $messageServer + ServerError $errorServer",Toast.LENGTH_LONG).show()


            } catch (e: Exception) {
                Log.d(ContentValues.TAG, e.message ?: " not exception")


            }
        }
        sealed class NetworkResult<T>(
            val data: T? = null,
            val message: String? = null
        ) {

            class Success<T>(data: T) : NetworkResult<T>(data)

            class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

            class Loading<T> : NetworkResult<T>()

        }
    }
}