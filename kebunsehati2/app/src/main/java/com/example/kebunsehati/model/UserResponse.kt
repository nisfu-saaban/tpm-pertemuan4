package com.example.kebunsehati.model

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("error")
	val error: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("koordinat")
	val koordinat: String? = null
)