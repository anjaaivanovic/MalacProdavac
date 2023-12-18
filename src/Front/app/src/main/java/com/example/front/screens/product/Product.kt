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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.front.components.GalleryComponent
import com.example.front.components.MyDropdownWorkingHours
import com.example.front.components.ProductImage
import com.example.front.components.Sidebar
import com.example.front.components.ToggleImageButton
import com.example.front.model.DTO.ImageDataDTO
import com.example.front.model.product.ProductReviewUserInfo
import com.example.front.navigation.Screen
import com.example.front.screens.myshop.getMultipartBodyPart
import com.example.front.ui.theme.DarkBlue
import com.example.front.ui.theme.Typography
import com.example.front.viewmodels.product.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rememberToastHostState
import java.lang.Integer.max

@SuppressLint("SuspiciousIndentation")
@Composable
fun ProductPage(
    navHostController: NavHostController,
    productViewModel: ProductViewModel,
    productID: Int
) {

    var quantity by remember { mutableStateOf(1) }
    val toastHostState = rememberToastHostState()
    val coroutineScope = rememberCoroutineScope()
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedSizeId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    var selectedImage by remember {
        mutableStateOf(
            ImageDataDTO(0, "placeholder.png")
        )
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris?.let { selectedUris ->
            CoroutineScope(Dispatchers.IO).launch {
                val pictures = selectedUris.mapNotNull { uri ->
                    try {
                        getMultipartBodyPart(context, uri)
                    } catch (e: Exception) {
                        null
                    }
                }

                withContext(Dispatchers.Main) {
                    productViewModel.uploadImages(1, productID, pictures)
                }
            }
        }
    }



    LaunchedEffect(Unit) {
        productViewModel.getUserId()?.let { productViewModel.getProductInfo(productID, it) }
        productViewModel.getReviewsForProduct(productID, 0)
    }

    DisposableEffect(productID) {
        onDispose {
            productViewModel.resetReviewState()
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

                if (productInfo?.images?.size != 0) {
                    selectedImage = productInfo?.images?.get(0)!!
                }


                LaunchedEffect(productInfo)
                {
                    if (productInfo?.images?.size != 0) {
                        selectedImage = productInfo.images[0];
                    }
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

                    ToggleImageButton(modifier = Modifier.align(Alignment.TopEnd))

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
                            GalleryComponent(
                                modifier = Modifier.padding(20.dp),
                                images = images,
                                selectedImage = selectedImage
                            ) { selectedImage = it }
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onBackground,
                                    shape = CircleShape
                                )
                                .padding(8.dp)
                                .clickable {
                                    photoPicker.launch("image/*")
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Image",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
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
                                        modifier = Modifier
                                            .padding(5.dp),
                                        style = Typography.titleSmall,
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF457FA8)
                                    )
                                }

                                Image(
                                    painter = painterResource(R.drawable.strelicaproduct),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = "${productInfo?.price} rsd",
                            modifier = Modifier
                                .padding(20.dp)
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

                        productInfo?.description?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                style = Typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF2E2E2E)
                            )
                        }

                        if (productInfo?.sizes != null && productInfo.sizes[0].size != "None") {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                productInfo.sizes.forEach {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .border(1.dp, Color.Gray, RectangleShape)
                                                .background(
                                                    color = if (it.size == selectedSize) Color.Gray else Color.White
                                                )
                                                .clickable {
                                                    selectedSize = it.size
                                                    selectedSizeId = it.sizeId
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
                                    }
                                }
                            }
                        }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
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
                                    "${getDayName(workingHours.day)} ${workingHours.openingHours} - ${workingHours.closingHours}"
                                }
                            MyDropdownWorkingHours(
                                workingHoursStrings?.get(0) ?: "",
                                workingHoursStrings ?: listOf(),
                                modifier = Modifier.weight(1f)
                            )
                        }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quantity",
                                style = Typography.titleSmall,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )

                            NumberPicker(
                                value = quantity,
                                onValueChange = { newValue -> quantity = newValue },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    println("VELICINE   " + productInfo?.sizes)
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
                                    .height(100.dp)
                                    .width(350.dp)
                                    .padding(20.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFFE48359))
                            ) {
                                Text(
                                    text = "Add To Cart",
                                    style = Typography.titleSmall,
                                    color = Color.White
                                )
                            }
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp),
                            color = Color.Gray
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(30.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Product reviews", style = Typography.bodyLarge)
                                Row {
                                    if (productInfo != null) {
                                        Text(
                                            text = "${productInfo.rating}",
                                            style = Typography.bodyLarge
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star icon"
                                    )
                                }
                            }
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(horizontal = 16.dp),
                                color = Color.Gray
                            )
                            Row(modifier = Modifier.padding(10.dp)) {
                                val reviews = productViewModel.stateReview.value.reviews

                                if (reviews.isNullOrEmpty()) {
                                    Text(
                                        "No reviews available",
                                        style = Typography.bodyLarge,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                } else {
                                    Column {
                                        ReviewCard(productReviewUserInfo = reviews[0])

                                        var reviewCounter by remember { mutableStateOf(1) }
                                        var showAllReviews by remember { mutableStateOf(false) }

                                        if (showAllReviews) {
                                            for (i in 1..reviewCounter) {
                                                ReviewCard(productReviewUserInfo = reviews[i - 1])
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            Arrangement.Center
                                        ) {

                                            Button(
                                                onClick = {
                                                    showAllReviews = !showAllReviews

                                                    if (showAllReviews) {
                                                        GlobalScope.launch {
                                                            productViewModel.getReviewsForProduct(
                                                                productID,
                                                                reviews.size
                                                            )
                                                        }
                                                    }
                                                }
                                            ) {
                                                Text(if (showAllReviews) "Show less reviews" else "Show all reviews")
                                            }
                                        }

                                        DisposableEffect(reviewCounter) {
                                            if (showAllReviews) {
                                                GlobalScope.launch {
                                                    productViewModel.getReviewsForProduct(
                                                        productID,
                                                        reviews.size
                                                    )
                                                }
                                            }
                                            onDispose {
                                            }
                                        }
                                    }
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
    }
    ToastHost(hostState = toastHostState)
}

@Composable
fun NumberPicker(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(modifier)
    ) {
        Button(
            onClick = {
                val newValue = max(value - 1, 1)
                onValueChange(newValue)
            },
            modifier = Modifier
                .height(30.dp)
                .weight(0.7f)
                .border(1.dp, Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        ) {
            Text(text = "-", color = MaterialTheme.colorScheme.onSurface)
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
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )

        Button(
            onClick = {
                val newValue = value + 1
                onValueChange(newValue)
            },
            modifier = Modifier
                .height(30.dp)
                .weight(0.7f)
                .border(1.dp, Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        ) {
            Text(text = "+", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}


fun getDayName(day: Int): String {
    return when (day) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        else -> "Unknown Day"
    }
}

@Composable
fun ReviewCard(productReviewUserInfo: ProductReviewUserInfo) {
    Card(
        modifier = Modifier
            .width(350.dp)
            .clip(RoundedCornerShape(20.dp))
            .padding(5.dp),
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