package com.example.front.screens.orders

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.front.R
import com.example.front.components.FourTabs
import com.example.front.components.OrderCard
import com.example.front.components.Sidebar
import com.example.front.components.SmallElipseAndTitle
import com.example.front.viewmodels.orders.OrdersViewModel

@Composable
fun OrdersScreen(navController: NavHostController,ordersViewModel: OrdersViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedTabIndex by remember{
        mutableStateOf(0)
    }
    LaunchedEffect(Unit)
    {
        ordersViewModel.getUserId()?.let { ordersViewModel.getOrders(it, null, null) }
    }
    LaunchedEffect(selectedTabIndex) {
        if(selectedTabIndex == 0)
        {
            ordersViewModel.getUserId()?.let { ordersViewModel.getOrders(it, null, null) }
        }
        else{
            ordersViewModel.getUserId()?.let { ordersViewModel.getOrders(it, selectedTabIndex, null) }
        }
    }
//    Sidebar(
//        drawerState,
//        navController,
//        shopViewModel.dataStoreManager
//    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    )
    {
        item{
            SmallElipseAndTitle(title = "Orders", drawerState = drawerState)
        }
        item {
            FourTabs(
                onFirstTabSelected = { selectedTabIndex = 0 },
                onSecondTabSelected = { selectedTabIndex = 1 },
                onThirdTabSelected = { selectedTabIndex = 2 },
                onFourthTabSelected = { selectedTabIndex = 3},
                selectedColumnIndex = selectedTabIndex,
                firstTab = "All",
                secondTab = "Delivered",
                thirdTab = "Pending",
                fourthTab = "Ready for pickup",
                isFilters = true
            )
        }
        item{
            if(ordersViewModel.state.value.isLoading)
            {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top=170.dp),
                    verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            else if(ordersViewModel.state.value.error.contains("NotFound"))
            {
                Column(modifier = Modifier.fillMaxSize().padding(top=170.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(id = R.drawable.nofound), contentDescription = null, modifier = Modifier.size(200.dp))
                    Text("No orders found", style = MaterialTheme.typography.titleSmall)
                }
            }
            else{
                Orders(ordersViewModel)
            }
        }
    }
}

@Composable
fun Orders(ordersViewModel: OrdersViewModel) {

    var orders = ordersViewModel.state.value.orders
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        orders.forEach { order ->
            OrderCard(
                orderid = "Order No${order.id}",
                quantity = order.quantity,
                amount = order.amount,
                date = order.createdOn,
                status = order.status
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    //OrderCard(orderid = "Order No1037088", quantity = 2, amount = 300.00f, date = "20.1.2023", status="Delivered")
}
