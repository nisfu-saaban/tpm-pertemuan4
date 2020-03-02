package com.example.kebunsehati.model

import com.google.gson.annotations.SerializedName

data class ProdukResponse(

	@field:SerializedName("id_kategori")
	val idKategori: String? = null,

	@field:SerializedName("produk")
	val produk: ArrayList<ProdukItem>? = null,

	@field:SerializedName("nama_kategori")
	val namaKategori: String? = null
)