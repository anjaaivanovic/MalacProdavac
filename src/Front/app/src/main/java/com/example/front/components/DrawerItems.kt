package com.example.front.components

import com.example.front.R
import com.example.front.navigation.Screen

val DrawerItems = listOf(
    DrawerItem(icon = R.drawable.navbar_profile, label = "Profile", route = Screen.MyProfile.route, secondaryLabel = "",roleId=1),
    DrawerItem(icon = R.drawable.navbar_home, label = "Home", route = Screen.Home.route, secondaryLabel = "",roleId=1),
    DrawerItem(icon = R.drawable.navbar_shop2, label = "My shop", route = Screen.MyShop.route, secondaryLabel = "",roleId=2),
    DrawerItem(icon = R.drawable.order, label = "Order requests/Orders", route = Screen.RequestsForShop.route, secondaryLabel = "",roleId=2),
    DrawerItem(icon = R.drawable.navbar_car, label = "Deliveries", route = Screen.Delivery.route, secondaryLabel = "",roleId=3),
//    DrawerItem(icon = R.drawable.navbar_profile, label = "Delivery profile", route = Screen.Home.route, secondaryLabel = "",roleId=3),
    DrawerItem(icon = R.drawable.navbar_package, label = "Products", route = Screen.AllProduct.route, secondaryLabel = "",roleId=1),
    DrawerItem(icon = R.drawable.navbar_shop1, label = "Shops", route = Screen.AllSellers.route, secondaryLabel = "",roleId=1),
    DrawerItem(icon = R.drawable.navbar_cart2, label = "My cart", route = "ordering", secondaryLabel = "",roleId=1),
    DrawerItem(icon = R.drawable.navbar_cart1, label = "My orders", route = Screen.Orders.route, secondaryLabel = "",roleId=1),
    DrawerItem(icon = R.drawable.navbar_bell, label = "Notifications", route = Screen.Notification.route, secondaryLabel = "",roleId=1),
    DrawerItem(icon = R.drawable.navbar_message, label = "Messages", route = Screen.Chats.route, secondaryLabel = "",roleId=1),
)
var filteredItems = emptyList<DrawerItem>()

data class DrawerItem(
    val icon: Int,
    val label: String,
    val route: String,
    val secondaryLabel: String,
    val roleId: Int
)