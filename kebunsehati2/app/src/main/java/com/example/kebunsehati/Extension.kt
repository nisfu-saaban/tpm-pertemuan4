package com.example.kebunsehati

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun Context.toast(msg: String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

fun Context.intent(cls: Class<*>){
   startActivity(Intent(this,cls))
}

fun <T> Call<T>.request(onError: (t: Throwable) -> Unit = {}, onResponse: (response: T?) -> Unit) {
    this.enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            onError(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            onResponse(response.body())
            Log.d("code","${response.code()}")

        }
    })
}