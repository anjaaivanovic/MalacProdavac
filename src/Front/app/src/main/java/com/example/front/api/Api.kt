package com.example.front.api

import com.example.front.model.DTO.AddProductReviewDTO
import com.example.front.model.DTO.ApiResponse
import com.example.front.model.DTO.CategoriesDTO
import com.example.front.model.DTO.CheckAvailabilityReqDTO
import com.example.front.model.DTO.CheckAvailabilityResDTO
import com.example.front.model.DTO.ChosenCategoriesDTO
import com.example.front.model.DTO.DeliveryPersonDTO
import com.example.front.model.DTO.EditShopDTO
import com.example.front.model.DTO.HomeProductDTO
import com.example.front.model.DTO.LeaveReviewDTO
import com.example.front.model.DTO.LoginDTO
import com.example.front.model.DTO.MetricsDTO
import com.example.front.model.DTO.NewOrder
import com.example.front.model.DTO.NewProductDTO
import com.example.front.model.DTO.NewProductDisplayDTO
import com.example.front.model.DTO.NewRoute
import com.example.front.model.DTO.NewShopDTO
import com.example.front.model.DTO.NotificationDTO
import com.example.front.model.DTO.OrderInfoDTO
import com.example.front.model.DTO.OrdersDTO
import com.example.front.model.DTO.OrdersPagesDTO
import com.example.front.model.DTO.PageCountDTO
import com.example.front.model.DTO.ProductDTO
import com.example.front.model.DTO.ProductDisplayDTO
import com.example.front.model.DTO.ReqForDeliveryPersonDTO
import com.example.front.model.DTO.RequestsForShopDTO
import com.example.front.model.DTO.ReviewDTO
import com.example.front.model.DTO.RouteDetails
import com.example.front.model.DTO.ShopDTO
import com.example.front.model.DTO.ShopDetailsCheckoutDTO
import com.example.front.model.DTO.ShopDetailsDTO
import com.example.front.model.DTO.ShopPagesDTO
import com.example.front.model.DTO.SubscribeDTO
import com.example.front.model.DTO.ToggleLikeDTO
import com.example.front.model.DTO.Trip
import com.example.front.model.DTO.UserRateDTO
import com.example.front.model.product.ProductInfo
import com.example.front.model.product.ProductReviewUserInfo
import com.example.front.model.request.RegistrationRequest
import com.example.front.model.response.Id
import com.example.front.model.response.LoginResponse
import com.example.front.model.response.Success
import com.example.front.model.response.SuccessBoolean
import com.example.front.model.response.SuccessInt
import com.example.front.model.user.MyProfileDTO
import com.example.front.model.user.UserEditDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface Api {
    @Headers("Content-Type: application/json")
    @POST("back/Auth/Login")
    suspend fun getLoginInfo(
        @Body login: LoginDTO
    ): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("back/Auth/Register")
    suspend fun register(
        @Body registrationRequest: RegistrationRequest
    ): Response<LoginResponse>

    @GET("back/Home/GetCategories")
    suspend fun getCategories(

    ): Response<List<CategoriesDTO>>

    @POST("/back/Home/SaveChosenCategories")
    suspend fun saveChosenCategories(
        @Body chosenCategories: ChosenCategoriesDTO
    ): Response<Boolean>

    @GET("back/Home/GetHomeProducts")
    suspend fun getHomeProducts(
        @Query("id") id: Int
    ): Response<List<HomeProductDTO>>

    @GET("back/Home/GetHomeShops")
    suspend fun getHomeShops(
        @Query("id") id: Int
    ): Response<List<ShopDTO>>

    @Headers("Content-Type: application/json")
    @GET("/back/Product/ProductDetails")
    suspend fun getProductDetails(
        @Query("productId") productID: Int,
        @Query("userId") userID: Int
    ): Response<ProductInfo>

    @GET("back/User/MyProfile")
    suspend fun getMyProfileInfo(
        @Query("userId") productID: Int,
    ): Response<MyProfileDTO>

    @PUT("back/Auth/Edit")
    suspend fun editUserProfile(
        @Body data: UserEditDTO
    ): Response<LoginResponse>

    @POST("back/Shop/ToggleLike")
    suspend fun toggleLike(
        @Query("shopId") shopId: Int,
        @Query("userId") userId: Int,
    ): Response<ToggleLikeDTO>

    @GET("/back/Product/ProductReviews")
    suspend fun getReviewsForProduct(
        @Query("productId") productId: Int,
        @Query("page") page: Int
    ): Response<List<ProductReviewUserInfo>>

    @GET("back/Shop/GetShops")
    suspend fun getShops(
        @Query("userId") userId: Int,
        @Query("categories") categories: List<Int>?,
        @Query("rating") rating: Int?,
        @Query("open") open: Boolean?,
        @Query("range") range: Int?,
        @Query("location") location: String?,
        @Query("sort") sort: Int,
        @Query("search") search: String?,
        @Query("page") page: Int,
        @Query("favorite") favorite: Boolean?,
        @Query("currLat") currLat: Float?,
        @Query("currLong") currLong: Float?
    ): Response<List<ShopDTO>>

    @PUT("/back/Auth/FCMTokenSave")
    suspend fun saveFCMToken(
        @Query("userId") userID: Int,
        @Query("token") token: String
    ): Response<Boolean>

    @GET("back/Shop/ShopPages")
    suspend fun getShopPages(
        @Query("userId") userId: Int,
        @Query("categories") categories: List<Int>?,
        @Query("rating") rating: Int?,
        @Query("open") open: Boolean?,
        @Query("range") range: Int?,
        @Query("location") location: String?,
        @Query("search") search: String?,
        @Query("favorite") favorite: Boolean?,
        @Query("currLat") currLat: Float?,
        @Query("currLong") currLong: Float?
    ): Response<ShopPagesDTO>

    @GET("back/Shop/ShopDetails")
    suspend fun getShopDetails(
        @Query("shopId") shopId: Int,
        @Query("userId") userId: Int
    ): Response<ShopDetailsDTO>

    @GET("back/Product/GetProducts")
    suspend fun getProducts(
        @Query("userId") userId: Int,
        @Query("categories") categories: List<Int>?,
        @Query("rating") rating: Int?,
        @Query("open") open: Boolean?,
        @Query("range") range: Int?,
        @Query("location") location: String?,
        @Query("sort") sort: Int?,
        @Query("search") search: String?,
        @Query("page") page: Int?,
        @Query("specificShopId") specificShopId: Int?,
        @Query("favorite") favorite: Boolean?,
        @Query("currLat") currLat: Float?,
        @Query("currLong") currLong: Float?
    ): Response<List<ProductDTO>>

    @GET("back/Shop/ShopReviews")
    suspend fun getShopReviews(
        @Query("shopId") shopId: Int,
        @Query("page") page: Int
    ): Response<List<ReviewDTO>>

    @POST("back/Shop/Review")
    suspend fun postShopReview(
        @Body data: LeaveReviewDTO,
    ): Response<SuccessBoolean>

    @GET("/back/Helper/Metrics")
    suspend fun getMetrics(
    ): Response<List<MetricsDTO>>

    @POST("/back/Product/AddProduct")
    suspend fun postNewProduct(
        @Body data: NewProductDTO,
    ): Response<Id>

    @GET("back/Shop/GetShopid")
    suspend fun getShopId(
        @Query("userId") userId: Int,
    ): Response<Id>

    @POST("/back/Helper/UploadImage")
    @Multipart
    suspend fun uploadImage(
        @Query("type") shopId: Int,
        @Query("id") page: Int,
        @Part image: MultipartBody.Part
    ): Response<Success>

    @GET("back/Shop/GetProductDisplay")
    suspend fun getProductDisplay(
        @Query("id") id: Int,
    ): Response<ProductDisplayDTO>

    @DELETE("back/Shop/DeleteProductDisplay")
    suspend fun deleteProductDisplay(
        @Query("id") id: Int,
    ): Response<SuccessBoolean>

    @GET("back/Shop/GetShopsForCheckout")
    suspend fun getShopsForCheckout(
        @Query("shopIds") shopIds: List<Int>
    ): Response<List<ShopDetailsCheckoutDTO>>

    @GET("back/Order/GetOrders")
    suspend fun getOrders(
        @Query("userId") userId: Int,
        @Query("status") status: Int?,
        @Query("page") shopIds: Int?
    ): Response<List<OrdersDTO>>

    @GET("back/Order/GetOrdersPageCount")
    suspend fun getOrdersPage(
        @Query("userId") userId: Int,
        @Query("status") status: Int?
    ): Response<OrdersPagesDTO>

    @POST("back/Shop/NewProductDisplay")
    suspend fun newProductDisplay(
        @Body data: NewProductDisplayDTO
    ): Response<SuccessBoolean>

    @GET("back/Order/OrderDetails")
    suspend fun getOrderInfo(
        @Query("orderId") orderId: Int
    ): Response<OrderInfoDTO>

    @GET("/back/Delivery/GetRouteDetails")
    suspend fun getRouteDetails(
        @Query("routeId") routeId: Int
    ): Response<RouteDetails>

    @DELETE("back/Shop/DeleteShop")
    suspend fun deleteShop(
        @Query("shopId") shopId: Int
    ): Response<SuccessBoolean>

    @GET("/back/Delivery/GetRoutesForDeliveryPerson")
    suspend fun GetRoutesForDeliveryPerson(
        @Query("userId") userId: Int
    ): Response<List<Trip>>

    @POST("/back/Shop/NewShop")
    suspend fun newShop(
        @Body data : NewShopDTO
    ):Response<SuccessInt>

    @POST("/back/Helper/UploadImage")
    @Multipart
    suspend fun uploadImage2(
        @Query("type") shopId: Int,
        @Query("id") page: Int,
        @Part image: MultipartBody.Part
    ): Response<SuccessBoolean>

    @POST("back/Order/CheckStock")
    suspend fun checkProductsAvailability(
        @Body products: List<CheckAvailabilityReqDTO>
    ): Response<List<CheckAvailabilityResDTO>>



    @POST("back/Order/InsertOrders")
    suspend fun insertOrders(
        @Body orders: List<NewOrder>
    ): Response<Unit>

    @GET("/back/Notification/GetNotifications")
    suspend fun getNotifications(
        @Query("userId") userId: Int,
        @Query("types") type: List<Int>?,
        @Query("page") page: Int,
    ): Response<List<NotificationDTO>>

    @PUT("back/Shop/EditShop")
    suspend fun editShop(
        @Body edit : EditShopDTO
    ): Response<SuccessBoolean>

    @POST("back/User/Rate")
    suspend fun userRate(
        @Body userRate: UserRateDTO
    ):Response<SuccessBoolean>

    @Headers("Content-Type: application/json")
    @POST("/back/Product/Review")
    suspend fun submitReview(
        @Body data : AddProductReviewDTO
    ): Response<SuccessBoolean>
    @DELETE("/back/Notification/DeleteNotification")
    suspend fun deleteNotification(
        @Query("notificationId") notificationId: Int
    ): Response<SuccessBoolean>
    @POST("/back/Product/Review")
    suspend fun productReview(
        @Body data : LeaveReviewDTO
    ): Response<SuccessBoolean>

    @GET("/back/Notification/PageCount")
    suspend fun getNotificationPageCount(
        @Query("userId") userId: Int,
        @Query("types") type: List<Int>?
    ): Response<PageCountDTO>

    @GET("/back/Delivery/GetRequestsForShop")
    suspend fun getRequestsForShopDTO(
        @Query("userId") userId: Int
    ): Response<List<RequestsForShopDTO>>
    @GET("/back/Delivery/GetDeliveryPeopleForRequest")
    suspend fun getDeliveriesPersonForRequest(
        @Query("requestId") requestId: Int
    ): Response<List<DeliveryPersonDTO>>

    @PUT("/back/Delivery/ChooseDeliveryPerson")
    suspend fun chooseDeliveryPerson(
        @Query("requestId") requestId: Int,
        @Query("chosenPersonId") chosenPersonId: Int
    ): Response<Boolean>

    @POST("back/Product/ToggleLike")
    suspend fun toggleLikeProduct(
        @Query("productId") shopId: Int,
        @Query("userId") userId: Int,
    ): Response<ToggleLikeDTO>

    @PUT("/back/Product/EditProduct")
    suspend fun editProduct(
        @Body editedProduct : NewProductDTO
    ): Response<Boolean>

    @GET("back/Order/GetShopOrders")
    suspend fun getShopOrders(
        @Query("ownerId") ownerId: Int,
        @Query("status") status: Int?,
        @Query("page") page: Int,
    ):Response<List<OrdersDTO>>

    @GET("back/Delivery/GetRequestsForDeliveryPerson")
    suspend fun getReqForDelivery(
        @Query("deliveryPerson") deliveryPerson: Int,
    ):Response<List<ReqForDeliveryPersonDTO>>

    @PUT("/back/Delivery/DeclineRequest")
    suspend fun declineReq(
        @Query("reqId") reqId: Int,
    ):Response<SuccessBoolean>

    @PUT("/back/Delivery/AddToRoute")
    suspend fun acceptReq(
        @Query("reqId") reqId: Int,
        @Query("routeId") routeId: Int,
    ):Response<SuccessBoolean>
    @PUT("back/Notification/MarkAsRead")
    suspend fun notificationMarkAsRead(
        @Query("notificationId") notificationId: Int
    ):ApiResponse

    @POST("back/Delivery/InsertDeliveryRoute")
    suspend fun newRoute(
        @Body newRoute: NewRoute
    ): ApiResponse

    @PUT("/back/Order/RespondToPickupRequest")
    suspend fun respondToPickupRequest(
        @Query("orderId") orderId: Int,
        @Query("resp") resp: Int,
        @Query("message") message: String
    ):Response<Boolean>

    @POST("/back/Product/Subscribe")
    suspend fun subscribeNotification(
        @Body subscribeDTO: SubscribeDTO
    ):Response<Boolean>
}