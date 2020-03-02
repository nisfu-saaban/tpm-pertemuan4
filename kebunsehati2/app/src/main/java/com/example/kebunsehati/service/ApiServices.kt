package com.example.kebunsehati.service

import com.example.kebunsehati.model.Kategori_Member
import com.example.kebunsehati.model.ProdukResponse
import com.example.kebunsehati.model.UserResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiServices {

    @Multipart
    @POST("userSignIn.html")
    fun userLogin(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody
    ): Call<UserResponse>

    @Multipart
    @POST("userSignUp.html")
    fun userRegister(
        @Part("nama") nama: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("pic") pic: RequestBody,
        @Part("no_hp") no_hp: RequestBody,
        @Part("no_wa") no_wa: RequestBody,
        @Part("email") email: RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("koordinat") koordinat: RequestBody
    ): Call<UserResponse>

    @POST("getIdKategori.html")
    fun getIdKategoriMember(): Call<Kategori_Member>

    @Multipart
    @POST("getDataProduct.html")
    fun showListKategoryProduct(
        @Part("idkategori_member") idKategoriMember: RequestBody
    ): Call<ArrayList<ProdukResponse>>

    @Multipart
    @POST("getDataProduct.html")
    fun tampilDataProduk(
        @Part("idkategori_member") idKategoriMember: RequestBody
    ): Call<ProdukResponse>
}