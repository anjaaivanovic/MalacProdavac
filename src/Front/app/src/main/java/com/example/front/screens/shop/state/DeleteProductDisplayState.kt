package com.example.front.screens.shop.state

import com.example.front.model.response.SuccessBoolean

data class DeleteProductDisplayState (
    var isLoading : Boolean = true,
    var success : SuccessBoolean? = null,
    var error : String = ""
)