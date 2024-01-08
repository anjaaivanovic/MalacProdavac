package com.example.front.screens.notification

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.front.R
import com.example.front.components.CardButton
import com.example.front.components.CommentsTextBox
import com.example.front.components.Paginator
import com.example.front.components.Sidebar
import com.example.front.components.SmallElipseAndTitle
import com.example.front.screens.shop.StarRating
import com.example.front.ui.theme.Typography
import com.example.front.viewmodels.notification.NotificationViewModel
import kotlinx.coroutines.launch
import rememberToastHostState

@Composable
fun NotificationScreen(navController: NavHostController, viewModel: NotificationViewModel) {

    var userId by remember { mutableStateOf(0) }
    var currentPage by remember { mutableStateOf(viewModel.stateCurrentPage.value) }//{ derivedStateOf { viewModel.stateCurrentPage.value } }
    var totalPages by remember { mutableStateOf (viewModel.statePageCount.value.pageCount?.count ?:0 ) }
    val toastHostState = rememberToastHostState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(Unit) {
        userId = viewModel.dataStoreManager.getUserIdFromToken()!!
        viewModel.getNotifications(userId, listOf(), currentPage)
    }

    if(viewModel.statePageCount.value.pageCount != null)
    {
        currentPage=1
        totalPages = viewModel.statePageCount.value.pageCount!!.count
    }

    if (viewModel.stateUserRate.value.userRate != null) {
        coroutineScope.launch {
            try {
                viewModel.inicijalnoStanje()

                viewModel.dataStoreManager.getUserIdFromToken()
                    ?.let { viewModel.getNotifications(it, listOf(), currentPage) }

                toastHostState.showToast("You have successfully rated a person!")

            } catch (e: Exception) {

                Log.e("ToastError", "Error showing toast", e)
            }
        }
    } else if (viewModel.stateUserRate.value.error.isNotEmpty()) {
        coroutineScope.launch {
            try {
                toastHostState.showToast("Oops! Fill in all fields!")
            } catch (e: Exception) {
                Log.e("ToastError", "Error showing toast", e)
            }
            viewModel.inicijalnoStanje()
        }
    }

    if (viewModel.stateProductReview.value.productReview != null) {
        coroutineScope.launch {
            try {
                viewModel.dataStoreManager.getUserIdFromToken()
                    ?.let { viewModel.getNotifications(it, listOf(), currentPage) }

                toastHostState.showToast("You have successfully rated a person!")

            } catch (e: Exception) {
                Log.e("ToastError", "Error showing toast", e)
            }
        }
    } else if (viewModel.stateProductReview.value.error.isNotEmpty()) {
        coroutineScope.launch {
            try {
                toastHostState.showToast("Oops! Fill in all fields!")
            } catch (e: Exception) {
                Log.e("ToastError", "Error showing toast", e)
            }
        }
    }
    if (viewModel.statePostReview.value.review != null) {
        coroutineScope.launch {
            try {
                viewModel.dataStoreManager.getUserIdFromToken()
                    ?.let { viewModel.getNotifications(it, listOf(), currentPage) }

                toastHostState.showToast("You have successfully rated a person!")

            } catch (e: Exception) {
                Log.e("ToastError", "Error showing toast", e)
            }
        }
    } else if (viewModel.statePostReview.value.error.isNotEmpty()) {
        coroutineScope.launch {
            try {
                toastHostState.showToast("Oops! Fill in all fields!")
            } catch (e: Exception) {
                Log.e("ToastError", "Error showing toast", e)
            }
        }
    }

    Sidebar(
        drawerState,
        navController,
        viewModel.dataStoreManager
    ) {
        if (viewModel.state.value.isLoading && viewModel.statePageCount.value.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                item {
                    SmallElipseAndTitle("Notifications", drawerState)

                    TypeOfNotifications(viewModel, userId)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Today",
                            style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
//                        Text(
//                            text = "Clear all",
//                            style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
//                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (viewModel.state.value.notifications.isEmpty()) {
                            Text(
                                text = "No notifications",
                                modifier = Modifier
                                    .padding(20.dp),
                                style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            )
                        }
                        viewModel.state.value.notifications.forEach { notification ->
                            NotificationCard(
                                title = notification.title,
                                body = notification.text,
                                type = notification.typeId,
                                refId = notification.referenceId,
                                viewModel = viewModel,
                                userId = userId,
                                notId = notification.id,
                                isRead = notification.read
                            )
                            {
                                viewModel.deleteNotification(notification.id)
                            }
                        }
                    }

                    Paginator(
                        currentPage = currentPage,
                        totalPages = totalPages,
                        onPageSelected = { newPage ->
                            if (newPage in 1..totalPages!!) {
                                viewModel.ChangePage(userId, newPage)
                                currentPage = newPage
                            }
                        }
                    )
                }
            }
        }
    }
}

data class NotificationCategory(
    val ids: List<Int>,
    val text: String
)

@Composable
fun TypeOfNotifications(viewModel: NotificationViewModel, userId: Int) {
    val items = listOf(
        NotificationCategory(listOf(), "All"),
        NotificationCategory(listOf(0, 2, 3), "Info"),
        NotificationCategory(listOf(1, 7, 8), "Reviews"),
        NotificationCategory(listOf(5, 6, 9), "Deals/Deliveries/Orders"),
    )

    LazyRow {
        items(items) { item ->
            ItemType(item.ids, item.text, viewModel, userId, viewModel.state.value.isLoading)
        }
    }
}

@Composable
fun ItemType(listOfIds: List<Int>, text: String, viewModel: NotificationViewModel, userId: Int, isLoading: Boolean) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable(enabled = !isLoading) {
                if (!isLoading) {
                    viewModel.getNotifications(userId, listOfIds, 1)
                }
            }
    ) {
        Column(
            modifier = Modifier.background(color=MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(text = text, modifier = Modifier.padding(16.dp, bottom = 0.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            else
            {
                if (listOfIds == viewModel.stateCurrentList.value.lista) {
                    Image(
                        painterResource(id = R.drawable.crtica),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(16.dp, top = 0.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationCard(
    title: String,
    body: String,
    type: Int,
    refId: Int,
    viewModel: NotificationViewModel,
    userId: Int,
    notId: Int,
    isRead: Boolean,
    onDismissed: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val read = remember { mutableStateOf(false) }
    var showViewDialog by remember {
        mutableStateOf(false)
    }
    var showRateUserDialog by remember {
        mutableStateOf(false)
    }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 300.dp.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)
    if (swipeableState.currentValue == 0) {
        Card(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                ),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = if(isRead || read.value) MaterialTheme.colorScheme.surface else Color(0xFFF4E5E0)),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = if (isRead || read.value) MaterialTheme.colorScheme.surface else Color(
                            0xFFF4E5E0
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_people),
                        contentDescription = "Left Icon",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "$title")
                    if (isRead || read.value)
                        Box(modifier = Modifier.size(24.dp))
                    else
                        Icon(
                            painter = painterResource(id = R.drawable.carbon_dot_mark),
                            contentDescription = "Right Icon",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                }

                Text(
                    text = "$body",
                    modifier = Modifier
                        .padding(10.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Text(
                        text = "View",
                        modifier = Modifier
                            .padding(0.dp, top = 16.dp, bottom = 5.dp)
                            .clickable {
                                coroutineScope.launch {
                                    read.value = viewModel.markAsRead(notId)
                                }
                                showViewDialog = true
                            },
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 16.sp
                        )
                    )
                    if (type == 1 || type == 7 || type == 8) {
                        Text(
                            text = "Rate",
                            modifier = Modifier
                                .padding(0.dp, top = 16.dp, bottom = 5.dp)
                                .clickable { showRateUserDialog = true },
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
    if (swipeableState.currentValue == 1) {
        onDismissed()
    }
    if (showViewDialog) {
        ViewDialog(
            onDismiss = { showViewDialog = false; },
            title,
            body,
            type,
            refId,
            viewModel,
            userId,
            notId
        )
    }
    if (showRateUserDialog) {
        RateUserDialog(
            onDismiss = { showRateUserDialog = false; },
            body,
            refId,
            viewModel,
            userId,
            notId,
            type
        )
    }
}

@Composable
fun RateUserDialog(
    onDismiss: () -> Unit,
    body: String,
    refId: Int,
    viewModel: NotificationViewModel,
    userId: Int,
    notId: Int,
    type: Int
) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    var selectedRatingC by remember { mutableStateOf(0) }
    var selectedRatingR by remember { mutableStateOf(0) }
    var selectedRatingO by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        onDismiss()
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->

                        }
                    }
                    .padding(top = 5.dp)
                    .align(Alignment.Center),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    Column(

                    )
                    {
                        Text(
                            text = "$body",
                            modifier = Modifier
                                .padding(10.dp),
                            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                        if (type == 9) {
                            Column {
                                Button(
                                    onClick = {
                                        viewModel.respondToPickupRequest(refId, 1, "Accepted")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Accept")
                                }

                                Spacer(Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        viewModel.respondToPickupRequest(refId, 1, "Not accepted")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Decline")
                                }
                            }
                        }

                        // -- USER RATE --
                        if (type == 1) {
                            Text(
                                text = "Communication",
                                modifier = Modifier
                                    .padding(10.dp, bottom = 0.dp),
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp)
                            )
                            StarRating { rating ->
                                selectedRatingC = rating
                            }
                            Text(
                                text = "Reliability",
                                modifier = Modifier
                                    .padding(10.dp, bottom = 0.dp, top = 10.dp),
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp)
                            )
                            StarRating { rating ->
                                selectedRatingR = rating
                            }
                            Text(
                                text = "Overall Experience",
                                modifier = Modifier
                                    .padding(10.dp, bottom = 0.dp, top = 10.dp),
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp)
                            )
                            StarRating { rating ->
                                selectedRatingO = rating
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        // -- PRODUCT & SHOP REVIEW --
                        if (type == 7 || type == 8) {
                            Column(
                                modifier = Modifier.heightIn(200.dp, 230.dp)
                            ) {
                                StarRating { rating ->
                                    selectedRatingC = rating
                                }
                                CommentsTextBox(
                                    onReviewTextChanged = { newText -> comment = newText },
                                    placeholder = "Leave a review"
                                )
                            }
                        }
                        if (type != 9) {
                            CardButton(
                                text = "Rate",
                                onClick = {
                                    if (type == 1) {
                                        viewModel.rateUser(
                                            userId,
                                            refId,
                                            selectedRatingC,
                                            selectedRatingR,
                                            selectedRatingO,
                                            notId
                                        )
                                    } else if (type == 7) {
                                        viewModel.rateProduct(
                                            refId,
                                            userId,
                                            selectedRatingC,
                                            comment,
                                            notId
                                        )
                                    } else if (type == 8) {
                                        viewModel.leaveShopReview(
                                            refId,
                                            userId,
                                            selectedRatingC,
                                            comment,
                                            notId
                                        )
                                    }
                                    onDismiss()
                                },
                                width = 1f,
                                modifier = Modifier.height(50.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ViewDialog(
    onDismiss: () -> Unit,
    title: String,
    body: String,
    type: Int,
    refId: Int,
    viewModel: NotificationViewModel,
    userId: Int,
    notId: Int
) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    var showRateUserDialog by remember {
        mutableStateOf(false)
    }
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        onDismiss()
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->

                        }
                    }
                    .padding(top = 5.dp)
                    .align(Alignment.Center),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$title",
                            modifier = Modifier
                                .padding(10.dp),
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "$body",
                            modifier = Modifier
                                .padding(10.dp)
                        )
                        if(type==1 || type==7 || type==8)
                        {
                            Text(
                                text = "Rate",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable { showRateUserDialog = true },
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
                            )
                        }
                        if(type == 9){
                            Text(
                                text = "View",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable { showRateUserDialog = true },
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
                            )
                        }
                    }
                }
            }
        }
    }
    if (showRateUserDialog) {
        RateUserDialog(
            onDismiss = { showRateUserDialog = false;onDismiss() },
            body,
            refId,
            viewModel,
            userId,
            notId,
            type
        )
    }
}



