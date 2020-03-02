package com.example.kebunsehati

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kebunsehati.Preferences.PreferencesHelper
import com.example.kebunsehati.Preferences.PreferencesKey
import com.example.kebunsehati.model.Kategori_Member
import com.example.kebunsehati.model.UserResponse
import com.example.kebunsehati.service.Services
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var pref :PreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        pref = PreferencesHelper(this)
        cekStatusLogin()
        bt_register.setOnClickListener {
            register()
        }
        getIdMember()
    }

    private fun register(){
        val username = et_username.text.toString()
        val password = et_password.text.toString()
        val nama = et_name.text.toString()
        val email = et_email.text.toString()
        val alamat = et_alamat.text.toString()
        val no_hp = et_nomor_telp.text.toString()
        val no_wa = et_nomor_wa.text.toString()

        val usernameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), username)
        val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        val namaBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nama)
        val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val alamatBody = RequestBody.create("text/plain".toMediaTypeOrNull(), alamat)
        val no_hpBody = RequestBody.create("text/plain".toMediaTypeOrNull(), no_hp)
        val no_waBody = RequestBody.create("text/plain".toMediaTypeOrNull(), no_wa)
        val koorBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "")

        Services().apiServices.userRegister(namaBody,alamatBody,
            namaBody,no_hpBody,no_waBody,emailBody,usernameBody,
            passwordBody,koorBody).enqueue(object : Callback<UserResponse>{
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val user = response.body()
                simpanDataLogin(user?.username!!)
                intent(BigMenuActivity::class.java)
                finish()
            }
        })
    }

    private fun getIdMember(){
        Services().apiServices.getIdKategoriMember().enqueue(object : Callback<Kategori_Member>{
            override fun onFailure(call: Call<Kategori_Member>, t: Throwable) {
                t.message
            }

            override fun onResponse(
                call: Call<Kategori_Member>, response: Response<Kategori_Member>) {
                val idMember = response.body()
                simpanIdMember(idMember?.idkategori_member!!)
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

    private fun simpanIdMember(id: String){
        pref.putString(PreferencesKey.IDMEMBER,id)
    }
}
