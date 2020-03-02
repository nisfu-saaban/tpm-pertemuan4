package com.example.kebunsehati.model

import com.google.gson.annotations.SerializedName

data class ProdukItem(
    @SerializedName("nama_produk")
    val namaProduk: String?,
    @SerializedName("status_stock")
    val statusStock: String?,
    @SerializedName("harga")
    val harga: String?,
    @SerializedName("thumbnail")
    val thumbnail: String?,
    @SerializedName("status_produk")
    val statusProduk: String?,
    @SerializedName("id_produk")
    val idProduk: String?,
    @SerializedName("best_seller")
    val bestSeller: String?,
    @SerializedName("meta_url")
    val metaUrl: String?,
    @SerializedName("status_kategoriPro")
    val statusKategoriPro: String?,
    @SerializedName("status_label")
    val statusLabel: String?,
    @SerializedName("nama_label")
    val namaLabel: String?,
    @SerializedName("qty")
    val qty: String?,
    @SerializedName("idmaster_satuan")
    val idmaster_satuan: String?,
    @SerializedName("nama_satuan")
    val namaSatuan: String?

)