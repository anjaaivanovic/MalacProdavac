package com.example.front.model.user

import com.example.front.model.DTO.RatingDTO
import com.google.gson.annotations.SerializedName

data class MyProfileDTO (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") var image: String,
    @SerializedName("role") val role: String,
    @SerializedName("roleId") val roleId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("address") var address: String,
    @SerializedName("email") val email: String,
    @SerializedName("rating") val rating: RatingDTO,
    @SerializedName("createdOn") val createdOn: String,
    @SerializedName("moneySpent") val moneySpent: Float,
    @SerializedName("moneyEarned") val moneyEarned: Float,
)