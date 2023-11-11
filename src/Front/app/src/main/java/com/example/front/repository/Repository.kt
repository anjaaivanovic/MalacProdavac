package com.example.front.repository

import com.example.front.api.Api
import com.example.front.model.DTO.CategoriesDTO
import com.example.front.model.DTO.ChosenCategoriesDTO
import com.example.front.model.DTO.HomeProductDTO
import com.example.front.model.DTO.LoginDTO
import com.example.front.model.request.RegistrationRequest
import com.example.front.model.response.LoginResponse
import com.example.front.model.DTO.ShopDTO
import com.example.front.model.product.ProductInfo
import com.example.front.model.user.MyProfileDTO
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(private val api: Api) {
    suspend fun getLogin(login: LoginDTO): Response<LoginResponse> {
        return api.getLoginInfo(login)
    }

    suspend fun register(registrationRequest: RegistrationRequest): Response<LoginResponse> {
        return api.register(registrationRequest)
    }

    suspend fun getCategories(): Response<List<CategoriesDTO>> {
        return api.getCategories()
    }

    suspend fun postCategories(categories: ChosenCategoriesDTO): Response<Boolean> {
        return api.saveChosenCategories(categories)
    }

    suspend fun getHomeProducts(id: Int): Response<List<HomeProductDTO>> {
        return api.getHomeProducts(id)
    }

    suspend fun getHomeShops(id: Int): Response<List<ShopDTO>> {
        return api.getHomeShops(id)
    }

    suspend fun getProductInfo(productId: Int, userId:Int): Response<ProductInfo> {
        return api.getProductDetails(productId,userId)
    }

    suspend fun getMyProfileInfo(userId:Int): Response<MyProfileDTO>{
        return api.getMyProfileInfo(userId)
    }
}