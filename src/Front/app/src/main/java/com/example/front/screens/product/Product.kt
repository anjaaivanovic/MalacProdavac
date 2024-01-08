package com.example.front.screens.product

import ToastHost
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.front.R
import com.example.front.components.CardButton
import com.example.front.components.CommentsTextBox
import com.example.front.components.GalleryComponent
import com.example.front.components.ImageItem
import com.example.front.components.MyDropdownWorkingHours
import com.example.front.components.ProductImage
import com.example.front.components.ReviewCard
import com.example.front.components.Sidebar
import com.example.front.model.DTO.CheckAvailabilityResDTO
import com.example.front.model.DTO.ImageDataDTO
import com.example.front.model.product.ProductReviewUserInfo
import com.example.front.navigation.Screen
import com.example.front.screens.myshop.getMultipartBodyPart
import com.example.front.screens.shop.StarRating
import com.example.front.ui.theme.DarkBlue
import com.example.front.ui.theme.Typography
import com.example.front.viewmodels.oneshop.OneShopViewModel
import com.example.front.viewmodels.product.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rememberToastHostState
import java.lang.Integer.max
import java.text.DecimalFormat

@SuppressLint("SuspiciousIndentation")
@Composable
fun ProductPage(
    navHostController: NavHostController,
    productViewModel: ProductViewModel,
    oneShopViewModel: OneShopViewModel,
    productID: Int
) {
    var showReviews by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf(false) }
    var reviewPage by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1) }
    val toastHostState = rememberToastHostState()
    val coroutineScope = rememberCoroutineScope()
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedSizeId by remember { mutableStateOf<Int?>(null) }
    var isToggled by remember { mutableStateOf (false) }
    var leaveAReview by remember { mutableStateOf(false) }
    var comment by remember {
        mutableStateOf("")
    }
    var feedback by remember { mutableStateOf(false) }

    var selectedRating by remember { mutableStateOf(0) }
    var showAddProduct by remember { mutableStateOf(false) }

    var currentPage = 1

    var flag = 1

    var selectedImage by remember { mutableStateOf(ImageDataDTO(-1,"placeholder.png")) }

    var userId by remember {mutableStateOf(0)}
    LaunchedEffect(Unit) {
        productViewModel.getUserId()?.let { productViewModel.getProductInfo(productID, it); userId = it }
        productViewModel.getReviewsForProduct(productID, currentPage)
    }

    DisposableEffect(productID) {
        onDispose {
            productViewModel.resetReviewState()
            flag = 1
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Sidebar(drawerState, navHostController, productViewModel.dataStoreManager) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (productViewModel.state.value.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val productInfo = productViewModel.state.value.product

                if (productInfo?.images?.size != 0 && flag == 1) {
                    selectedImage = productInfo?.images?.get(0)!!
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.5f)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent)
                ) {
                    if (productInfo != null && selectedImage != null) {
                        selectedImage?.let { image ->
                            if (image.image.isNotEmpty()) {
                                ProductImage(image.image, modifier = Modifier)
                            }
                        }
                    }

                    if (productInfo?.isOwner != true) {
                        val currentImage = if (isToggled) painterResource(id = R.drawable.srcefull)
                        else painterResource(id = R.drawable.srce)

                        Image(
                            painter = currentImage,
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(5.dp)
                                .align(Alignment.TopEnd)
                                .clickable {
                                    isToggled = !isToggled
                                    productViewModel.updateLikeStatus(isToggled)
                                    productViewModel.toggleLike(productID, userId)
                                }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(5.dp)
                                .align(Alignment.TopEnd)
                                .clickable{ showAddProduct = true}
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.backarrow),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .align(Alignment.TopStart)
                            .clickable { navHostController.popBackStack() }
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                        .background(Color.Transparent)
                        .border(
                            1.dp,
                            Color.Transparent,
                            shape = RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 30.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .background(Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxSize()
                            .padding(
                                top = 20.dp,
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 0.dp
                            )
                            .verticalScroll(rememberScrollState())
                    ) {
                        productInfo?.images?.let { images ->
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, top = 10.dp, bottom = 20.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(images) { image ->
                                    Box(
                                        modifier = Modifier
                                            .clickable {
                                                selectedImage = image
                                                flag+=1
                                            }
                                            .border(2.dp, if (image == selectedImage) MaterialTheme.colorScheme.primary else Color.Transparent, shape = RoundedCornerShape(10.dp))
                                            .clip(RoundedCornerShape(10.dp))
                                    ) {
                                        val imageUrl = "http://softeng.pmf.kg.ac.rs:10015/images/${image.image}"

                                        val painter: Painter = rememberAsyncImagePainter(model = imageUrl)
                                        Image(
                                            painter = painter,
                                            contentDescription = null,
                                            modifier = Modifier.size(60.dp)
                                        )
                                    }
                                }
                            }
                        }


                        productInfo?.name?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                style = Typography.titleMedium,
                                color = DarkBlue
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Row(modifier = Modifier.clickable {
                                navHostController.navigate("${Screen.Shop.route}/${productInfo?.shopId}")
                            }) {
                                productInfo?.shopName?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier,
                                        style = Typography.titleSmall,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }

                                Icon(
                                    painter = painterResource(R.drawable.strelicaproduct),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(22.dp)
                                        .padding(start = 5.dp),
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }

                        Text(
                            text = "${DecimalFormat("0.00").format(productInfo?.price)} rsd",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            style = Typography.titleLarge,
                            color = DarkBlue,
                            textAlign = TextAlign.Center
                        )

                        productInfo?.saleMessage?.let {
                            Text(
                                text = it,
                                modifier = Modifier.fillMaxWidth(),
                                style = Typography.titleMedium,
                                textAlign = TextAlign.Center,
                                color = Color(0xFFE15F26)
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp),
                            color = Color.Gray
                        )


                        productInfo?.description?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                style = Typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (productInfo?.sizes != null && productInfo.sizes[0].size != "None") {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                productInfo.sizes.forEach {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .background(
                                                    color = if (it.size == selectedSize) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.background
                                                )
                                                .clickable {
                                                    coroutineScope.launch {
                                                        val result: CheckAvailabilityResDTO? = productViewModel.isAvailable(productInfo.productId!!, it.size ?: "None", quantity)
                                                        if(result != null && result.available >= result.quantity) {
                                                            selectedSize = it.size
                                                            selectedSizeId = it.sizeId
                                                        } else if (result != null){
                                                            try {
                                                                quantity = if (result.available > 0) result.available else 1
                                                                toastHostState.showToast(
                                                                    "Currently available: ${result.available}",
                                                                    Icons.Default.Clear
                                                                )
                                                            } catch (e: Exception) {
                                                                Log.e("ToastError", "Error showing toast", e)
                                                            }
                                                        }
                                                    }
                                                }
                                                .padding(4.dp)
                                        ) {
                                            Text(
                                                text = it.size,
                                                fontSize = 24.sp,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                }
                            }
                        }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp,end=10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pick up time:",
                                style = Typography.titleSmall,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            val workingHoursStrings =
                                productInfo?.workingHours?.map { workingHours ->
                                    "${getDayName(workingHours.day)} ${workingHours.openingHours.substring(0, 5)} - ${workingHours.closingHours.substring(0, 5)}"
                                }
                            MyDropdownWorkingHours(
                                workingHoursStrings?.get(0) ?: "",
                                workingHoursStrings ?: listOf(),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quantity",
                                style = Typography.titleSmall,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            if (productInfo?.sizes?.all { it.quantity == 0 } != true)
                                NumberPicker(
                                    value = quantity,
                                    onValueChange = { newValue -> quantity = newValue },
                                    modifier = Modifier.weight(1f)
                                )
                            else
                                Text(
                                    text = "Out of stock",
                                    style = Typography.titleSmall,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                        }
                        if(productInfo?.sizes?.all { it.quantity == 0 } != true)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        if (productInfo?.name != null &&
                                            productInfo?.price != null &&
                                            productInfo.shopId != null &&
                                            productInfo?.shopName != null &&
                                            productInfo.images?.isNotEmpty() == true &&
                                            productInfo.metric != null &&
                                            (
                                                    productInfo.sizes?.isEmpty() == true ||
                                                            (productInfo.sizes != null && productInfo.sizes[0].size == "None") ||
                                                            (selectedSize != null && selectedSizeId != null)
                                                    )
                                        ) {
                                            ////proverava da li je na stanju
                                            coroutineScope.launch {
                                                val result: CheckAvailabilityResDTO? = productViewModel.isAvailable(productInfo.productId!!, selectedSize ?: "None", quantity)
                                                if(result != null && result.available >= result.quantity){
                                                    productViewModel.addToCart(
                                                        productID,
                                                        productInfo.name,
                                                        productInfo.price,
                                                        quantity,
                                                        productInfo.shopId,
                                                        productInfo.shopName,
                                                        productInfo.images[0].image,
                                                        productInfo.metric,
                                                        selectedSize ?: "None",
                                                        selectedSizeId ?: 0
                                                    )

                                                    coroutineScope.launch {
                                                        try {
                                                            toastHostState.showToast(
                                                                "Product added to cart",
                                                                Icons.Default.Check
                                                            )
                                                        } catch (e: Exception) {
                                                            Log.e("ToastError", "Error showing toast", e)
                                                        }
                                                    }
                                                } else if (result != null){
                                                    coroutineScope.launch {
                                                        try {
                                                            quantity = if (result.available > 0) result.available else 1
                                                            toastHostState.showToast(
                                                                "Currently available: ${result.available}",
                                                                Icons.Default.Clear
                                                            )
                                                        } catch (e: Exception) {
                                                            Log.e("ToastError", "Error showing toast", e)
                                                        }
                                                    }
                                                }
                                            }

                                        } else if (productInfo?.sizes?.isNotEmpty() == true || selectedSize != null) {
                                            coroutineScope.launch {
                                                try {
                                                    toastHostState.showToast(
                                                        "Please select size",
                                                        Icons.Default.Clear
                                                    )
                                                } catch (e: Exception) {
                                                    Log.e("ToastError", "Error showing toast", e)
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .height(90.dp)
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    shape = RoundedCornerShape(30),
                                    colors = ButtonDefaults.buttonColors(Color(0xFFE48359))
                                ) {
                                    Text(
                                        text = "Add To Cart",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.background),
                                        color = Color.White
                                    )
                                }
                            }
                        else
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = { productViewModel.subscribeNotification(productID, userId) },
                                    modifier = Modifier
                                        .height(90.dp)
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    shape = RoundedCornerShape(30),
                                    colors = ButtonDefaults.buttonColors(Color(0xFFE48359))
                                ) {
                                    Text(
                                        text = "Notify me when available",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.background),
                                        color = Color.White
                                    )
                                }
                            }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(horizontal = 16.dp),
                                color = Color.Gray
                            )

                            if (!productInfo?.isOwner!!) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Text(
                                        text = "Leave a review",
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 8.dp)
                                            .clickable {
                                                leaveAReview = !leaveAReview
                                            },
                                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                    )
                                    Icon(
                                        imageVector = if (leaveAReview) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clickable {
                                                leaveAReview = !leaveAReview
                                            }
                                            .padding(top = 8.dp, end = 5.dp)
                                            .size(30.dp),
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                if (leaveAReview) {
                                    if ((productViewModel.state.value.product!!.bought == true && productViewModel.state.value.product!!.rated == true) || feedback) {
                                        Text(
                                            "A review from you has already been submitted.",
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(top = 20.dp, start = 16.dp)
                                        )
                                    }
                                    else if (productViewModel.state.value.product!!.bought == false ) {
                                        Text(
                                            "You must buy something in order to leave a review!",
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(top = 20.dp, start = 16.dp)
                                        )
                                    }
                                  else {
                                        StarRating { rating ->
                                            selectedRating = rating
                                        }
                                        CommentsTextBox(
                                            onReviewTextChanged = { newText -> comment = newText },
                                            placeholder = "Leave a review"
                                        )
                                        CardButton(
                                            "Submit review", onClick = {
                                                productViewModel.submitReview(
                                                    productID,
                                                    selectedRating,
                                                    comment,
                                                    currentPage
                                                )

                                                feedback=true
                                            }, 0.9f,
                                            Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .height(50.dp), MaterialTheme.colorScheme.secondary
                                        )
                                    }

                                        if (!productViewModel.stateReviewSubmit.value.isLoading && toast) {
                                            feedback = true
                                            coroutineScope.launch {
                                                try {
                                                    feedback = true
                                                    toastHostState.showToast(
                                                        "Review successfully submitted",
                                                        Icons.Default.Clear
                                                    )
                                                } catch (e: Exception) {
                                                    Log.e("ToastError", "Error showing toast", e)
                                                }
                                            }
                                            productViewModel.getReviewsForProduct(
                                                productID,
                                                reviewPage
                                            )
                                            toast = false
                                        } else if (productViewModel.stateReviewSubmit.value.error.isNotEmpty() && toast) {
                                            coroutineScope.launch {
                                                try {
                                                    toastHostState.showToast("Error", Icons.Default.Clear)
                                                } catch (e: Exception) {
                                                    Log.e("ToastError", "Error showing toast", e)
                                                }
                                            }
                                            toast = false
                                        }

                                    }
                                }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Text(
                                text = "View all reviews",
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp)
                                    .clickable {
                                        showReviews = !showReviews
                                    },
                                style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
                            )
                            Icon(
                                imageVector = if (showReviews) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        showReviews = !showReviews
                                    }
                                    .padding(top = 8.dp, end = 5.dp)
                                    .size(30.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        if (showReviews) {
                            var rev = productViewModel.stateReview.value.reviews
                            if (rev != null) {
                                if (rev.isEmpty()) {
                                    Text(
                                        "No reviews found for this store.",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = 20.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .heightIn(100.dp, 1000.dp)
                            ) {
                                if (rev != null) {
                                    rev.forEach { review ->
                                        ReviewCard(
                                            username = review.username,
                                            imageRes = review.image,
                                            comment = review.comment,
                                            rating = review.rating
                                        )
                                    }
                                }
                            }

                        if (!productViewModel.stateReview.value.error.isNotEmpty()) {
                            Text("See more",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .clickable {
                                        reviewPage++; productViewModel.getReviewsForProduct(
                                        productID,
                                        reviewPage
                                    )
                                    }
                                    .align(Alignment.CenterHorizontally))
                        }
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp),
                            color = Color.Gray
                        )
                    }
                }
                    }
                }
        }
    if (showAddProduct) {
        EditProduct(onDismiss = { showAddProduct = false }, oneShopViewModel, productViewModel.state.value.product!!, navHostController)
    }
    ToastHost(hostState = toastHostState)
    }


@Composable
fun NumberPicker(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .weight(0.8f)
                .clickable {
                    val newValue = max(value - 1, 1)
                    onValueChange(newValue)
                }
                .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp))         ,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "-", color = MaterialTheme.colorScheme.primary, fontSize = 17.sp)
        }

        BasicTextField(
            value = value.toString(),
            onValueChange = {
                val newValue = it.toIntOrNull() ?: value
                onValueChange(newValue)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )

        Box(
            modifier = Modifier
                .height(40.dp)
                .weight(0.8f)
                .clickable {
                    val newValue = value + 1
                    onValueChange(newValue)
                }
                .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp))         ,
                contentAlignment = Alignment.Center
        ) {
            Text(text = "+", color = MaterialTheme.colorScheme.primary, fontSize = 17.sp)
        }
    }
}


fun getDayName(day: Int): String {
    return when (day) {
        1 -> "Mon"
        2 -> "Tue"
        3 -> "Wed"
        4 -> "Thu"
        5 -> "Fri"
        6 -> "Sat"
        7 -> "Sun"
        else -> "Unknown Day"
    }
}

@Composable
fun ReviewCard2(productReviewUserInfo: ProductReviewUserInfo) {
    Card(
        modifier = Modifier
            .width(350.dp)
            .clip(RoundedCornerShape(20.dp))
            .padding(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Row() {
                    val imageUrl =
                        "http://softeng.pmf.kg.ac.rs:10015/images/${productReviewUserInfo.image}"

                    val painter: Painter = rememberAsyncImagePainter(model = imageUrl)
                    Image(
                        painter = painter,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "${productReviewUserInfo.username}",
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
                    for (i in 1..productReviewUserInfo.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star icon"
                        )
                    }
                }
                Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
                    Text(text = "${productReviewUserInfo.comment}", style = Typography.bodySmall)
                }
            }
            Column {
                Row() {
                    Text(
                        text = "Posted ${productReviewUserInfo.getDaysSincePosted()} days ago",
                        style = Typography.bodySmall
                    )
                }
            }
        }
    }
}