package com.example.front.repository

import android.util.Log
import com.example.front.api.Api
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
import retrofit2.http.Query
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

    suspend fun getProductInfo(productId: Int, userId: Int): Response<ProductInfo> {
        return api.getProductDetails(productId, userId)
    }

    suspend fun getMyProfileInfo(userId: Int): Response<MyProfileDTO> {
        return api.getMyProfileInfo(userId)
    }

    suspend fun editProfile(editDTO: UserEditDTO): Response<LoginResponse> {
        return api.editUserProfile(editDTO)
    }

    suspend fun toggleLike(shopId: Int, userId: Int): Response<ToggleLikeDTO> {
        return api.toggleLike(shopId, userId)
    }

    suspend fun getReviewsForProduct(
        productId: Int,
        page: Int
    ): Response<List<ProductReviewUserInfo>> {
        return api.getReviewsForProduct(productId, page)
    }

    suspend fun getShops(
        userId: Int,
        categories: List<Int>?,
        rating: Int?,
        open: Boolean?,
        range: Int?,
        location: String?,
        sort: Int,
        search: String?,
        page: Int,
        favorite: Boolean,
        currLat: Float?,
        currLong: Float?
    ): Response<List<ShopDTO>> {
        return api.getShops(
            userId,
            categories,
            rating,
            open,
            range,
            location,
            sort,
            search,
            page,
            favorite,
            currLat,
            currLong
        )
    }

    suspend fun saveFCMToken(userID: Int, token: String): Response<Boolean> {
        Log.d("Repository", "USAO")
        val res = api.saveFCMToken(userID, token)
        Log.d("Odgovor", res.toString())
        return res
    }

    suspend fun getShopPages(
        userId: Int,
        categories: List<Int>?,
        rating: Int?,
        open: Boolean?,
        range: Int?,
        location: String?,
        search: String?,
        favorite: Boolean,
        currLat: Float?,
        currLong: Float?
    ): Response<ShopPagesDTO> {
        return api.getShopPages(
            userId,
            categories,
            rating,
            open,
            range,
            location,
            search,
            favorite,
            currLat,
            currLong
        )
    }

    suspend fun getShopDetails(userId: Int, shopId: Int): Response<ShopDetailsDTO> {
        return api.getShopDetails(shopId, userId)
    }

    suspend fun getProducts(
        userId: Int,
        categories: List<Int>?,
        rating: Int?,
        open: Boolean?,
        range: Int?,
        location: String?,
        sort: Int?,
        search: String?,
        page: Int?,
        specificShopId: Int?,
        favorite: Boolean?,
        currLat: Float?,
        currLong: Float?
    ): Response<List<ProductDTO>> {
        return api.getProducts(
            userId,
            categories,
            rating,
            open,
            range,
            location,
            sort,
            search,
            page,
            specificShopId,
            favorite,
            currLat,
            currLong
        )
    }

    suspend fun getShopReviews(shopId: Int, page: Int): Response<List<ReviewDTO>> {
        return api.getShopReviews(shopId, page)
    }

    suspend fun postShopReview(
        id: Int,
        userId: Int,
        rating: Int?,
        comment: String
    ): Response<SuccessBoolean> {
        return api.postShopReview(LeaveReviewDTO(id, userId, rating, comment))
    }

    suspend fun getShopId(userId: Int): Response<Id> {
        return api.getShopId(userId)
    }

    suspend fun getMetrics(): Response<List<MetricsDTO>> {
        return api.getMetrics()
    }

    suspend fun postNewProduct(product: NewProductDTO): Response<Id> {
        return api.postNewProduct(product)
    }

    suspend fun uploadImage(type: Int, id: Int, imagePart: MultipartBody.Part): Response<Success> {
        return api.uploadImage(type, id, imagePart)
    }

    suspend fun getProductDisplay(id: Int): Response<ProductDisplayDTO> {
        return api.getProductDisplay(id)
    }

    suspend fun deleteProductDisplay(id: Int): Response<SuccessBoolean> {
        return api.deleteProductDisplay(id)
    }

    suspend fun getShopsForCheckout(shopIds: List<Int>): Response<List<ShopDetailsCheckoutDTO>> {
        return api.getShopsForCheckout(shopIds)
    }

    suspend fun getOrders(userId: Int, status: Int?, page: Int?): Response<List<OrdersDTO>> {
        return api.getOrders(userId, status, page)
    }

    suspend fun getOrdersPage(userId: Int, status: Int?): Response<OrdersPagesDTO> {
        return api.getOrdersPage(userId, status)
    }

    suspend fun newProductDisplay(newProductDisplayDTO: NewProductDisplayDTO): Response<SuccessBoolean> {
        return api.newProductDisplay(newProductDisplayDTO);
    }

    suspend fun getOrderInfo(orderId: Int): Response<OrderInfoDTO> {
        return api.getOrderInfo(orderId)
    }

    suspend fun getRouteDetails(routeId: Int): Response<RouteDetails> {
        return api.getRouteDetails(routeId)
    }

    suspend fun deleteShop(shopId: Int): Response<SuccessBoolean> {
        return api.deleteShop(shopId)
    }

    suspend fun checkProductsAvailability(products: List<CheckAvailabilityReqDTO>): Response<List<CheckAvailabilityResDTO>> {
        return api.checkProductsAvailability(products)
    }

    suspend fun GetRoutesForDeliveryPerson(userId: Int): Response<List<Trip>> {
        return api.GetRoutesForDeliveryPerson(userId)
    }

    suspend fun newShop(data: NewShopDTO): Response<SuccessInt> {
        return api.newShop(data)
    }

    suspend fun uploadImage2(
        type: Int,
        id: Int,
        imagePart: MultipartBody.Part
    ): Response<SuccessBoolean> {
        return api.uploadImage2(type, id, imagePart)
    }

    suspend fun insertOrders(orders: List<NewOrder>): Response<Unit> {
        return api.insertOrders(orders)
    }

    suspend fun editShop(EditShop: EditShopDTO): Response<SuccessBoolean> {
        return api.editShop(EditShop)
    }

    suspend fun getNotifications(
        userId: Int,
        type: List<Int>?,
        page: Int
    ): Response<List<NotificationDTO>> {
        return api.getNotifications(userId, type, page)
    }

    suspend fun userRate(userRate: UserRateDTO): Response<SuccessBoolean> {
        return api.userRate(userRate)
    }

    suspend fun productReview(productReview: LeaveReviewDTO): Response<SuccessBoolean> {
        return api.productReview(productReview)
    }

    suspend fun submitReview(
        productID: Int,
        userID: Int,
        rating: Int,
        comment: String
    ): Response<SuccessBoolean> {
        val x =
            AddProductReviewDTO(id = productID, userId = userID, rating = rating, comment = comment)
        return api.submitReview(x)
    }

    suspend fun deleteNotification(notificationId: Int): Response<SuccessBoolean> {
        return api.deleteNotification(notificationId)
    }

    suspend fun getNotificationPageCount(userId: Int, type: List<Int>?): Response<PageCountDTO> {
        return api.getNotificationPageCount(userId, type)
    }

    suspend fun getRequestsForShopDTO(shopId: Int): Response<List<RequestsForShopDTO>> {
        return api.getRequestsForShopDTO(shopId)
    }

    suspend fun getDeliveriesPersonForRequest(requestId: Int): Response<List<DeliveryPersonDTO>> {
        return api.getDeliveriesPersonForRequest(requestId)
    }

    suspend fun chooseDeliveryPerson(requestId: Int, deliveryPersonId: Int): Response<Boolean> {
        return api.chooseDeliveryPerson(requestId, deliveryPersonId)
    }

    suspend fun toggleLikeProduct(productId: Int, userId: Int): Response<ToggleLikeDTO> {
        return api.toggleLikeProduct(productId, userId)
    }

    suspend fun editProduct(product: NewProductDTO): Response<Boolean> {
        return api.editProduct(product)
    }

    suspend fun getShopOrders(ownerId: Int, status: Int?, page: Int): Response<List<OrdersDTO>> {
        return api.getShopOrders(ownerId, status, page)
    }

    suspend fun getReqForDelivery(deliveryPersonId: Int): Response<List<ReqForDeliveryPersonDTO>> {
        return api.getReqForDelivery(deliveryPersonId)
    }

    suspend fun declineRequest(reqId: Int): Response<SuccessBoolean> {
        return api.declineReq(reqId)
    }

    suspend fun acceptReq(reqId:Int, routeId: Int): Response<SuccessBoolean>
    {
        return api.acceptReq(reqId, routeId)
    }
    suspend fun markNotificationAsRead(notificationId: Int): ApiResponse {
        return api.notificationMarkAsRead(notificationId)
    }
    suspend fun addNewRoute(newRoute: NewRoute): ApiResponse{
        return api.newRoute(newRoute)
    }

    suspend fun respondToPickupRequest(
        orderId: Int,
        resp: Int,
        message: String
    ): Response<Boolean> {
        return api.respondToPickupRequest(orderId, resp, message)
    }

    suspend fun subscribeNotification(subscribeDTO: SubscribeDTO): Response<Boolean> {
        return api.subscribeNotification(subscribeDTO)
    }
}