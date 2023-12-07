package com.example.front.viewmodels.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.helper.DataStore.DataStoreManager
import com.example.front.repository.MongoRepository
import com.example.front.repository.Repository
import com.example.front.screens.home.states.HomeProductsState
import com.example.front.screens.home.states.HomeShopState
import com.example.front.screens.home.states.ToggleLikeState
import com.example.front.screens.orders.state.OrdersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: Repository,
    val dataStoreManager: DataStoreManager,
    private val mongoRepository: MongoRepository
) : ViewModel() {

    private val _state = mutableStateOf(OrdersState())
    var state: State<OrdersState> = _state;

    fun getOrders(userId: Int, status: Int?, page:Int?) {
        viewModelScope.launch {
            try {
                val response = repository.getOrders(userId,status,page)

                if (response != null) {
                    if (response.isSuccessful) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            orders = response.body()!!,
                            error = ""
                        )
                    }
                    else{
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "NotFound"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = true,
                    error = "Error"
                )
            }
        }
    }

    suspend fun getUserId(): Int? {
        return dataStoreManager.getUserIdFromToken()
    }
}