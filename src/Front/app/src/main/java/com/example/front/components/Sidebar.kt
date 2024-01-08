package com.example.front.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.front.MainActivity
import com.example.front.R
import com.example.front.helper.DataStore.DataStoreManager
import com.example.front.navigation.Screen
import com.example.front.navigation.SetupNavGraph
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sidebar(
    drawerState: DrawerState,
    navController: NavHostController,
    dataStoreManager: DataStoreManager,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(324.dp),
                drawerContainerColor = Color(0xFF294E68)
            ) {
                Spacer(
                    modifier = Modifier
                        .height(56.dp)
                )

                filteredItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(route = item.route)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                                modifier = Modifier.height(24.dp)
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFF294E68),
                            selectedContainerColor = Color(0xFF263e52),
                            unselectedTextColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White,
                            selectedIconColor = Color.White,
                        ),
                        shape = RectangleShape
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = null,
                        modifier = Modifier.height(24.dp)
                    )
                    Text(
                        text = "Logout",
                        modifier = Modifier
                            .clickable {
                                FirebaseMessaging.getInstance().deleteToken()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {

                                            FirebaseMessaging.getInstance().token
                                                .addOnCompleteListener { newTokenTask ->
                                                    if (newTokenTask.isSuccessful) {
                                                        val newToken = newTokenTask.result
                                                        GlobalScope.launch {
                                                            dataStoreManager.storeFCM(newToken)
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                runBlocking {
                                    dataStoreManager.storeToken("")
                                }
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(navController.graph.id) {
                                            inclusive = true
                                    }
                                }
                            },
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        },
        content = content
    )
}