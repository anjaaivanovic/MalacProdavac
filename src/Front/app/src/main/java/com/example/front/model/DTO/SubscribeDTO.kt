package com.example.front.model.DTO

import com.nimbusds.jose.shaded.gson.annotations.SerializedName

data class SubscribeDTO(
    @SerializedName("productId")
    val productId: Int,

    @SerializedName("userId")
    val userId: Int
)
