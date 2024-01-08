package com.example.front.viewmodels.product

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.helper.DataStore.DataStoreManager
import com.example.front.model.DTO.CheckAvailabilityReqDTO
import com.example.front.model.DTO.CheckAvailabilityResDTO
import com.example.front.model.DTO.ImageDataDTO
import com.example.front.model.DTO.SubscribeDTO
import com.example.front.model.product.ProductInCart
import com.example.front.repository.MongoRepository
import com.example.front.repository.Repository
import com.example.front.screens.myshop.state.ImageState
import com.example.front.screens.product.ProductProductState
import com.example.front.screens.product.ReviewProductState
import com.example.front.screens.product.SubmitReviewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: Repository,
    internal val dataStoreManager: DataStoreManager,
    private val mongoRepository: MongoRepository
) : ViewModel() {

    private val _state = mutableStateOf(ProductProductState())
    var state: State<ProductProductState> = _state;

    private val _stateReview = mutableStateOf(ReviewProductState())
    var stateReview: State<ReviewProductState> = _stateReview;//

    private val _stateReviewSubmit = mutableStateOf(SubmitReviewState())
    var stateReviewSubmit: State<SubmitReviewState> = _stateReviewSubmit;


    suspend fun getProductInfo(productID: Int, userID: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getProductInfo(productID, userID)
                if (response.isSuccessful) {
                    val productInfo = response.body()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        product = productInfo
                    )
                } else {

                }
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    fun getReviewsForProduct(productID: Int, pageNumber: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getReviewsForProduct(productID, pageNumber)
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    val reviewsResponse = response.body()
                    _stateReview.value = _stateReview.value.copy(
                        isLoading = false,
                        reviews = reviewsResponse,
                        error=""
                    )
                } else {
                    _stateReview.value = _stateReview.value.copy(
                        isLoading = false,
                        error="NotFound"
                    )
                }
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    fun toggleLike(productID: Int, userID: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleLikeProduct(productID, userID)
                Log.d("TOGGLE LIKE", response.toString())
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    fun ChangePage(currentPage: Int) {
        viewModelScope.launch {
            _state.value.product!!.productId?.let { getReviewsForProduct(it, currentPage) }
        }
    }

    fun resetReviewState() {
        _stateReview.value = ReviewProductState()
    }


    suspend fun getUserId(): Int? {
        return dataStoreManager.getUserIdFromToken()
    }

    fun addToCart(
        productID: Int,
        name: String,
        price: Float,
        quantity: Int,
        shopId: Int,
        shopName: String,
        image: String,
        metric: String,
        size: String,
        sizeId : Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productInCart = ProductInCart()
                productInCart.id = productID
                productInCart.name = name
                productInCart.price = price.toDouble()
                productInCart.quantity = quantity
                productInCart.shopId = shopId
                productInCart.shopName = shopName
                productInCart.image = image
                productInCart.metric = metric
                productInCart.size = size
                productInCart.sizeId = sizeId
                productInCart.available = quantity

                mongoRepository.updateProduct(productInCart)
            } catch (e: Exception) {
                Log.e("addToCart", "Greska prilikom dodavanja proizvoda u korpu: ${e.message}")
            }
        }
    }
    suspend fun isAvailable(id:Int, size: String, quantity: Int): CheckAvailabilityResDTO? {
        val list = listOf(CheckAvailabilityReqDTO(id, size, quantity))

        try {
            val response = repository.checkProductsAvailability(list)
            if (response.isSuccessful) {
                return response.body()!![0]
            }
        } catch (e: Exception) {
            println("Exception in isAvailable(): $e")
        }
        return null
    }

    fun submitReview(productID: Int,  rating: Int, comment: String, currentPage: Int) {
        viewModelScope.launch {
            try {
                val response = repository.submitReview(productID, dataStoreManager.getUserIdFromToken()!!, rating, comment)
                if (response.isSuccessful) {
                    val res = response.body()
                    _stateReviewSubmit.value = _stateReviewSubmit.value.copy(
                        isLoading = false,
                        review = res,
                        error = ""
                    )
                    getReviewsForProduct(productID, currentPage)
                } else {
                    _stateReviewSubmit.value = _stateReviewSubmit.value.copy(
                        isLoading = false,
                        review = null,
                        error = "NotFound"
                    )
                }
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    fun updateLikeStatus(isToggled: Boolean)
    {
        this.state.value.product!!.liked = isToggled
    }

    fun subscribeNotification (productId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.subscribeNotification(SubscribeDTO(productId, userId))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }
    }
}