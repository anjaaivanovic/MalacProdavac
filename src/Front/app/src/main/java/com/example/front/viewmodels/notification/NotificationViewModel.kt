package com.example.front.viewmodels.notification

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.helper.DataStore.DataStoreManager
import com.example.front.model.DTO.LeaveReviewDTO
import com.example.front.model.DTO.NotificationDTO
import com.example.front.model.DTO.PageCountDTO
import com.example.front.model.DTO.UserRateDTO
import com.example.front.repository.Repository
import com.example.front.screens.notification.state.ListaTabovaState
import com.example.front.screens.notification.state.ProductReviewState
import com.example.front.screens.notification.state.UserRateState
import com.example.front.screens.shop.state.PostReviewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val dataStoreManager: DataStoreManager,
    val repository: Repository
) : ViewModel() {

    private val _state = mutableStateOf(NotificationState())
    var state: State<NotificationState> = _state;

    private val _stateUserRate = mutableStateOf(UserRateState())
    var stateUserRate: State<UserRateState> = _stateUserRate;

    private val _stateProductReview = mutableStateOf(ProductReviewState())
    var stateProductReview: State<ProductReviewState> = _stateProductReview;

    private val _statePostReview = mutableStateOf(PostReviewState())
    var statePostReview: State<PostReviewState> = _statePostReview;

    private val _statePageCount = mutableStateOf(NotificationPageCountState())
    var statePageCount: State<NotificationPageCountState> = _statePageCount;

    private val _stateCurrentPage = mutableStateOf(1)
    var stateCurrentPage: State<Int> = _stateCurrentPage;

    private val _stateCurrentList = mutableStateOf(ListaTabovaState())
    var stateCurrentList: State<ListaTabovaState> = _stateCurrentList;

    fun inicijalnoStanje() {
        _stateUserRate.value = _stateUserRate.value.copy(
            isLoading = true,
            userRate = null,
            error = ""
        )
    }

    fun leaveShopReview(shopId: Int, userId: Int, rating: Int?, comment: String, notId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.postShopReview(shopId, userId, rating, comment)
                Log.d("PRODAVNICA", response.toString())
                if (response.isSuccessful) {
                    val rev = response.body()
                    _statePostReview.value = _statePostReview.value.copy(
                        isLoading = false,
                        review = response.body(),
                        error = ""
                    )
                    deleteNotification(notId)
                } else {
                    _statePostReview.value = _statePostReview.value.copy(
                        isLoading = false,
                        review = null,
                        error = "NotFound"
                    )
                }
            } catch (e: Exception) {
                _statePostReview.value = _statePostReview.value.copy(
                    error = e.message.toString()
                )
            }
        }
    }

    fun rateProduct(id: Int, userId: Int, rating: Int, comment: String, notId: Int) {
        viewModelScope.launch {
            try {
                val review = LeaveReviewDTO(id, userId, rating, comment)
                val response = repository.productReview(review)

                if (response.isSuccessful) {
                    _stateProductReview.value = _stateProductReview.value.copy(
                        isLoading = false,
                        productReview = response.body(),
                        error = ""
                    )
                    deleteNotification(notId)
                } else {
                    _stateProductReview.value = _stateProductReview.value.copy(
                        isLoading = false,
                        productReview = null,
                        error = "NotFound"
                    )
                }
            } catch (e: Exception) {
                _stateProductReview.value.error = e.message.toString()
            }
        }
    }

    fun rateUser(
        raterId: Int,
        ratedId: Int,
        communication: Int,
        reliability: Int,
        overallExperience: Int,
        notId: Int
    ) {
        viewModelScope.launch {
            try {
                val rateUser =
                    UserRateDTO(raterId, ratedId, communication, reliability, overallExperience)
                val response = repository.userRate(rateUser)
                if (response.isSuccessful) {
                    _stateUserRate.value = _stateUserRate.value.copy(
                        isLoading = false,
                        userRate = response.body(),
                        error = ""
                    )
                    deleteNotification(notId)
                } else {
                    _stateUserRate.value = _stateUserRate.value.copy(
                        isLoading = false,
                        userRate = null,
                        error = "NotFound"
                    )
                }
            } catch (e: Exception) {
                _stateUserRate.value.error = e.message.toString()
            }
        }
    }

    fun getNotifications(userId: Int, type: List<Int>?, page: Int) {
        _stateCurrentList.value = type?.let {
            stateCurrentList.value.copy(
                lista = it
            )
        }!!
        viewModelScope.launch {
            try {
                val response = repository.getNotifications(userId, type, page)
                Log.d("TEST", userId.toString() + "" + type.toString() + "" + page.toString())
                Log.d("TEST", response.toString())
                if (response != null) {
                    if (response.isSuccessful) {
                        _state.value = response.body()?.let {
                            _state.value.copy(
                                isLoading = false,
                                notifications = it
                            )
                        }!!
                        getNotificationPages(userId, type)
                        _stateCurrentPage.value = 1
                    }
                }
            } catch (e: Exception) {
                _state.value.error = e.message.toString()
            }
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(id)
            } catch (e: Exception) {
                Log.d("NotificationViewModel", e.message.toString())
            }
        }
    }

    fun getNotificationPages(userId: Int, type: List<Int>?) {
        viewModelScope.launch {
            try {
                val response = repository.getNotificationPageCount(userId, type)
                Log.d("Res", response.toString())
                _statePageCount.value = _statePageCount.value.copy(
                    isLoading = false,
                    pageCount = response.body()
                )
            } catch (e: Exception) {
                _statePageCount.value.error = e.message.toString()
            }
        }
    }

    fun ChangePage(userId: Int, page: Int) {
        getNotifications(userId, _stateCurrentList.value.lista, page)
    }

    suspend fun markAsRead(notId: Int): Boolean {
        try {
            val response = repository.markNotificationAsRead(notId)
            println(response)
            println(response.success)
            return response.success
        } catch (e: Exception) {
            println(e.message.toString())
        }
        return false
    }

    fun respondToPickupRequest(
        orderId: Int,
        resp: Int,
        message: String
    ) {
        viewModelScope.launch {
            try {
                val response = repository.respondToPickupRequest(orderId, resp, message)

            } catch (e: Exception) {
                Log.d("NotificationViewModel", e.message.toString())
            }
        }
    }
}
data class NotificationState (
    var isLoading : Boolean = true,
    var notifications : List<NotificationDTO> = listOf(),
    var error : String = ""
)

data class NotificationPageCountState(
    var isLoading: Boolean = true,
    var pageCount: PageCountDTO? = null,
    var error: String = ""
)