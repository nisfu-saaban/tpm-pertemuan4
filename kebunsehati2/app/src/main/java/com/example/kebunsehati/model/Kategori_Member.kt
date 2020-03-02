package com.example.kebunsehati.model

import com.google.gson.annotations.SerializedName

data class Kategori_Member(
    @SerializedName("id")
    val idkategori_member: String?,
    @SerializedName("nama")
    val nama_kategori: String?
)