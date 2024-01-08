package com.example.front.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.front.screens.RequestsForShopScreen.RequestsForShopScreen
import com.example.front.screens.cart.NewCreditCartScreen
import com.example.front.screens.categories.RegistrationCategories
import com.example.front.screens.chat.ChatPage
import com.example.front.screens.chat.ChatSessionsList
import com.example.front.screens.delivery.DeliveryScreen
import com.example.front.screens.delivery.RouteDetailsScreen
import com.example.front.screens.home.HomePage
import com.example.front.screens.myshop.MyShopScreen
import com.example.front.screens.myshop.SetUpShopScreen
import com.example.front.screens.notification.NotificationScreen
import com.example.front.screens.order.OrderScreen
import com.example.front.screens.orders.OrdersScreen
import com.example.front.screens.product.ProductPage
import com.example.front.screens.products.AllProducts
import com.example.front.screens.sellers.SellersScreen
import com.example.front.screens.shop.ShopScreen
import com.example.front.screens.userprofile.UserProfileScreen
import com.example.front.viewmodels.cart.CartViewModel
import com.example.front.viewmodels.categories.CategoriesViewModel
import com.example.front.viewmodels.chat.ChatViewModel
import com.example.front.viewmodels.checkout.CheckoutViewModel
import com.example.front.viewmodels.delivery.DeliveryViewModel
import com.example.front.viewmodels.delivery.RouteDetailsViewModel
import com.example.front.viewmodels.home.HomeViewModel
import com.example.front.viewmodels.login.LoginViewModel
import com.example.front.viewmodels.myprofile.MyProfileViewModel
import com.example.front.viewmodels.myshop.MyShopViewModel
import com.example.front.viewmodels.notification.NotificationViewModel
import com.example.front.viewmodels.oneshop.OneShopViewModel
import com.example.front.viewmodels.orderinfo.OrderInfoViewModel
import com.example.front.viewmodels.orders.OrdersViewModel
import com.example.front.viewmodels.product.ProductViewModel
import com.example.front.viewmodels.products.ProductsViewModel
import com.example.front.viewmodels.register.RegisterViewModel
import com.example.front.viewmodels.requestsshop.RequestsForShopViewModel
import com.example.front.viewmodels.shops.ShopsViewModel
import com.example.front.viewmodels.splasintro.SplashAndIntroViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val categoriesViewModel: CategoriesViewModel = hiltViewModel()
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val productViewModel: ProductViewModel = hiltViewModel()
    val splashViewModel: SplashAndIntroViewModel = hiltViewModel()
    val myProfileViewModel: MyProfileViewModel = hiltViewModel()
    val shopsViewModel: ShopsViewModel = hiltViewModel()
    val cartViewModel: CartViewModel = hiltViewModel()
    val oneShopViewModel: OneShopViewModel = hiltViewModel()
    val myShopViewModel: MyShopViewModel = hiltViewModel()
    val checkoutViewModel: CheckoutViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val ordersViewModel: OrdersViewModel = hiltViewModel()
    val orderInfoViewModel: OrderInfoViewModel = hiltViewModel()
    val deliveryViewModel: DeliveryViewModel = hiltViewModel()
    val routedetailsViewModel: RouteDetailsViewModel = hiltViewModel()
    val productsViewModel: ProductsViewModel = hiltViewModel()
    val chatViewModel: ChatViewModel = hiltViewModel()
    val requestsViewModel: RequestsForShopViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomePage(navController = navController, homeViewModel)
        }
        composable(
            route = Screen.Categories.route
        )
        {
            RegistrationCategories(navController = navController, categoriesViewModel)
        }
        composable(
            route = "${Screen.Product.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val productId = arguments.getInt("id")
            ProductPage(navController, productViewModel, oneShopViewModel,productId)
        }

        composable(
            route = Screen.RequestsForShop.route
        )
        {
            RequestsForShopScreen(requestsViewModel,navController)
        }

        composable(
            route = Screen.MyProfile.route
        )
        {
            UserProfileScreen(navController = navController, myProfileViewModel)
        }

        composable(
            route = Screen.AllProduct.route
        )
        {
            AllProducts(navController = navController, productsViewModel, oneShopViewModel)
        }
        composable(route = Screen.Delivery.route) {
            DeliveryScreen(navController, deliveryViewModel)
        }
        composable(
            route = Screen.AllSellers.route
        )
        {
            SellersScreen(navController = navController, shopsViewModel)
        }
        composable(
            route = "${Screen.Route.route}/{routeID}",
            arguments = listOf(navArgument("routeID") { type = NavType.IntType })
        ) { navBackStackEntry ->

            val arguments = requireNotNull(navBackStackEntry.arguments)
            val routeID = arguments.getInt("routeID")

            RouteDetailsScreen(navController, routedetailsViewModel, routeID)
        }
        composable(
            route = Screen.NewCreditCard.route
        )
        {
            NewCreditCartScreen(navController, checkoutViewModel)
        }
        composable(
            route = "${Screen.Shop.route}/{id}/{info}",
            arguments = listOf(navArgument("id") { type = NavType.IntType },
                navArgument("info") { type = NavType.IntType })
        )
        { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val productId = arguments.getInt("id")
            val info = arguments.getInt("info")
            ShopScreen(
                navController = navController,
                shopViewModel = oneShopViewModel,
                shopId = productId,
                info = info
            )
        }
        composable(route = Screen.Notification.route) {
            NotificationScreen(navController, notificationViewModel)
        }
        composable(
            route = Screen.MyShop.route
        )
        {
            MyShopScreen(navController = navController, myShopViewModel = myShopViewModel)
        }
        composable(
            route = "${Screen.SetUpShop.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        )
        { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val id = arguments.getInt("id")
            SetUpShopScreen(navController = navController, myShopViewModel, id)
        }
        composable(
            route = "chat/{chatId}/{user2Id}/{image}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("user2Id") { type = NavType.IntType },
                navArgument("image") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val chatId = arguments.getString("chatId")
            val user2Id = arguments.getInt("user2Id")
            val image = arguments.getString("image")

            ChatPage(chatViewModel, chatId!!, user2Id, myProfileViewModel, image!!, navController)
        }

        composable(
            route = Screen.Chats.route
        ) {
            ChatSessionsList(chatViewModel, navController)
        }
        composable(route = Screen.Orders.route)
        {
            OrdersScreen(navController = navController, ordersViewModel)
        }
        composable(
            route = "${Screen.Order.route}/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        )
        { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val orderId = arguments.getInt("orderId")
            OrderScreen(navController = navController, orderId, orderInfoViewModel)
        }
        //composable(
        //            route = "${Screen.Product.route}/{id}",
        //            arguments = listOf(navArgument("id") { type = NavType.IntType })
        //        ) { navBackStackEntry ->
        //            val arguments = requireNotNull(navBackStackEntry.arguments)
        //            val productId = arguments.getInt("id")
        //            ProductPage(navController, productViewModel, productId)
        //        }

        introNavGraph(navController = navController, splashViewModel)
        authNavGraph(
            navController = navController,
            loginViewModel = loginViewModel,
            registerViewModel = registerViewModel,
            categoriesViewModel
        )
        orderingNavGraph(navController, cartViewModel, checkoutViewModel)
    }
}