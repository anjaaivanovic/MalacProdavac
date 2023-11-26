package com.example.front.viewmodels.oneshop

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.helper.DataStore.DataStoreManager
import com.example.front.model.DTO.CategoriesDTO
import com.example.front.model.DTO.FiltersDTO
import com.example.front.model.DTO.MetricsDTO
import com.example.front.model.DTO.NewProductDTO
import com.example.front.repository.Repository
import com.example.front.screens.shop.state.GetCategoriesState
import com.example.front.screens.shop.state.GetMetricsState
import com.example.front.screens.shop.state.PostReviewState
import com.example.front.screens.shop.state.ProductState
import com.example.front.screens.shop.state.ShopReviewState
import com.example.front.screens.shop.state.ShopState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class OneShopViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = mutableStateOf(ShopState())
    var state: State<ShopState> = _state;

    private val _stateProduct = mutableStateOf(ProductState())
    var stateProduct: State<ProductState> = _stateProduct;

    // defaultni filteri
    private val _filtersState =
        mutableStateOf(FiltersDTO(0, listOf(), 0, null, 0, null, 0, "", 1, null, null, null))
    var filtersState: State<FiltersDTO> = _filtersState;

    private val _stateReview = mutableStateOf(ShopReviewState())
    var stateReview: State<ShopReviewState> = _stateReview;

    //ShopReviewState
    private val _statePostReview = mutableStateOf(PostReviewState())
    var statePostReview: State<PostReviewState> = _statePostReview;

    //CategoriesGetState
    private val _stateGetMetrics = mutableStateOf(GetMetricsState())
    var stateGetMetrics: State<GetMetricsState> = _stateGetMetrics;

    //CategoriesGetState
    private val _stateGetCategories = mutableStateOf(GetCategoriesState())
    var stateGetCategories: State<GetCategoriesState> = _stateGetCategories;

    suspend fun getShopDetails(userId: Int, shopId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getShopDetails(userId, shopId)
                if (response.isSuccessful) {
                    val shopp = response.body()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        shop = shopp
                    )
                } else {
                    _state.value = _state.value.copy(
                        error = "NotFound"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message.toString()
                )
            }
        }
    }

    fun getProducts(
        userId: Int,
        categories: List<Int>?,
        rating: Int?,
        open: Boolean?,
        range: Int?,
        location: String?,
        sort: Int,
        search: String?,
        page: Int,
        specificShop: Int?,
        favorite: Boolean?,
        currLat: Float?,
        currLong: Float?
    ) {
        inicijalnoStanje()
        viewModelScope.launch {
            try {
                Log.d("SALJEM", categories.toString())
                val response = repository.getProducts(
                    userId,
                    categories,
                    rating,
                    open,
                    range,
                    location,
                    sort,
                    search,
                    page,
                    specificShop,
                    favorite,
                    currLat,
                    currLong
                )
                _filtersState.value = _filtersState.value.copy(
                    userId = userId,
                    categories = categories,
                    rating = rating,
                    open = open,
                    range = range,
                    location = location,
                    sort = sort,
                    search = search,
                    page = page,
                    favorite = favorite,
                    currLat = currLat,
                    currLon = currLong
                )
                Log.d("KONACAN RESPONSE", response.toString())
                if (response.isSuccessful) {
                    val prod = response.body()
                    _stateProduct.value = _stateProduct.value.copy(
                        isLoading = false,
                        products = prod
                    )
                } else {
                    _stateProduct.value = _stateProduct.value.copy(
                        isLoading = false,
                        error = "NotFound"
                    )
                }
            } catch (e: Exception) {
                _stateProduct.value = _stateProduct.value.copy(
                    error = e.message.toString()
                )
            }
        }
    }

    fun getShopReview(shopId: Int, page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getShopReviews(shopId, page)
                if (response.isSuccessful) {
                    val rev = response.body()
                    _stateReview.value = _stateReview.value.copy(
                        isLoading = false,
                        reviews = rev!!,
                        error = ""
                    )
                } else {
                    _stateReview.value = _stateReview.value.copy(
                        error = "NotFound"
                    )
                }
            } catch (e: Exception) {
                _stateReview.value = _stateReview.value.copy(
                    error = e.message.toString()
                )
            }
        }
    }

    fun leaveReview(shopId: Int, userId: Int, rating: Int?, comment: String) {
        viewModelScope.launch {
            try {
                val response = repository.postShopReview(shopId, userId, rating, comment)
                Log.d("REVIEW RESPONSE", response.toString())
                if (response.isSuccessful) {
                    val rev = response.body()
                    _statePostReview.value = _statePostReview.value.copy(
                        isLoading = false,
                        review = response.body(),
                        error = ""
                    )
                } else {
                    _statePostReview.value = _statePostReview.value.copy(
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

    suspend fun getUserId(): Int? {
        return dataStoreManager.getUserIdFromToken()
    }

    fun changeToggleLike(userId: Int, shopId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleLike(userId, shopId)
            } catch (e: Exception) {
                Log.d("Error", "Error - cant change")
            }
        }
    }

    fun inicijalnoStanje() {
        _stateProduct.value = _stateProduct.value.copy(
            isLoading = true,
            products = emptyList(),
            error = ""
        )
    }

    fun withoutFilters() {
        _filtersState.value = _filtersState.value.copy(
            1, listOf(), null, null, 0, null, 0, null, 1
        )
    }

    fun changeCategories(categories: List<Int>?) {
        _filtersState.value = _filtersState.value.copy(
            categories = categories
        )
    }

    fun changeRating(rating: Int?) {
        _filtersState.value = _filtersState.value.copy(
            rating = rating
        )
    }

    fun changeOpen(open: Boolean?) {
        _filtersState.value = _filtersState.value.copy(
            open = open
        )
    }

    fun changeRange(range: Int?) {
        _filtersState.value = _filtersState.value.copy(
            range = range
        )
    }

    fun changeLocation(location: String?) {
        _filtersState.value = _filtersState.value.copy(
            location = location
        )
    }

    fun changeCoordinates(geoPoint: GeoPoint?) {
        _filtersState.value = _filtersState.value.copy(
            currLat = geoPoint!!.latitude.toFloat(),
            currLon = geoPoint!!.longitude.toFloat(),
        )
    }


    fun Sort(s: Int, specificShopId: Int?) {
        _filtersState.value = _filtersState.value.copy(
            sort = s
        )
        getProducts(
            _filtersState.value.userId,
            _filtersState.value.categories,
            _filtersState.value.rating,
            _filtersState.value.open,
            _filtersState.value.range,
            _filtersState.value.location,
            _filtersState.value.sort,
            _filtersState.value.search,
            _filtersState.value.page,
            specificShopId,
            _filtersState.value.favorite,
            _filtersState.value.currLat,
            _filtersState.value.currLon
        )
    }

    fun ChangePage(s: Int) {
        _filtersState.value = _filtersState.value.copy(
            page = s
        )
        //getShops(_filtersState.value.userId,_filtersState.value.categories,_filtersState.value.rating,_filtersState.value.open,_filtersState.value.range,_filtersState.value.location,_filtersState.value.sort,_filtersState.value.search,_filtersState.value.page, true, _filtersState.value.currLat, _filtersState.value.currLon)
    }

    fun Search(s: String, specificShopId: Int?) {
        _filtersState.value = _filtersState.value.copy(
            search = s
        )
        getProducts(
            _filtersState.value.userId,
            _filtersState.value.categories,
            _filtersState.value.rating,
            _filtersState.value.open,
            _filtersState.value.range,
            _filtersState.value.location,
            _filtersState.value.sort,
            _filtersState.value.search,
            _filtersState.value.page,
            specificShopId,
            _filtersState.value.favorite,
            _filtersState.value.currLat,
            _filtersState.value.currLon
        )
    }

    fun DialogFilters(
        categories: List<Int>?,
        rating: Int?,
        open: Boolean?,
        location: String?,
        range: Int?,
        specificShopId: Int?
    ) {
        _filtersState.value = _filtersState.value.copy(
            categories = categories,
            rating = rating,
            open = open,
            location = location,
            range = range
        )
        Log.d("STA SALJEM", filtersState.toString())
        getProducts(
            _filtersState.value.userId,
            _filtersState.value.categories,
            _filtersState.value.rating,
            _filtersState.value.open,
            _filtersState.value.range,
            _filtersState.value.location,
            _filtersState.value.sort,
            _filtersState.value.search,
            _filtersState.value.page,
            specificShopId,
            _filtersState.value.favorite,
            _filtersState.value.currLat,
            _filtersState.value.currLon
        )
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategories()
                if (response.isSuccessful) {
                    val rev = response.body()
                    _stateGetCategories.value = _stateGetCategories.value.copy(
                        isLoading = false,
                        categories = response.body(),
                        error = ""
                    )
                } else {
                    _stateGetCategories.value = _stateGetCategories.value.copy(
                        error = "No categories"
                    )
                }
            } catch (e: Exception) {
                _stateReview.value = _stateReview.value.copy(
                    error = e.message.toString()
                )
            }
        }
    }

    fun getMetrics() {
        viewModelScope.launch {
            try {
                val response = repository.getMetrics()
                if (response.isSuccessful) {
                    _stateGetMetrics.value = _stateGetMetrics.value.copy(
                        isLoading = false,
                        metrics = response.body(),
                        error = ""
                    )
                } else {
                    _stateGetCategories.value = _stateGetCategories.value.copy(
                        error = "No metrics"
                    )
                }
            } catch (e: Exception) {
                _stateReview.value = _stateReview.value.copy(
                    error = e.message.toString()
                )
            }
        }
    }
    fun postNewProduct(newProd: NewProductDTO){
        viewModelScope.launch {
            repository.postNewProduct(newProd)
        }
    }
}