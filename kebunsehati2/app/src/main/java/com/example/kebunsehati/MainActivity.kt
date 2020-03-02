package com.example.kebunsehati

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kebunsehati.Preferences.PreferencesHelper
import com.example.kebunsehati.Preferences.PreferencesKey
import com.example.kebunsehati.model.UserResponse
import com.example.kebunsehati.service.Services
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var pref :PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = PreferencesHelper(this)
        cekStatusLogin()
        tv_sign_up.setOnClickListener { intent(RegisterActivity::class.java) }
        bt_signin.setOnClickListener {
            doLogin(et_username_login.text.toString(),et_pass.text.toString())
        }
    }

    private fun doLogin( username: String , password: String) {
        val usernameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), username)
        val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

        Services().apiServices.userLogin(usernameBody, passwordBody)
            .enqueue(object : retrofit2.Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable){
                    toast("gagal melakukan login")
                }

                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
                {
                    val user = response.body()
                    simpanDataLogin(user?.username!!)
                    intent(BigMenuActivity::class.java)
                    finish()
                    Log.d("Code ", "${response.code()}")
                    toast("berhasil Login")
                }

            })
    }

    private fun cekStatusLogin(){
        val hasLogin = pref.getBoolean(PreferencesKey.HAS_LOGIN)
        if (hasLogin){
            intent(BigMenuActivity::class.java)
            finish()
        }
    }

    private fun simpanDataLogin(username: String){
        pref.putString(PreferencesKey.USERNAME,username)
        pref.putBoolean(PreferencesKey.HAS_LOGIN,true)
    }



}
