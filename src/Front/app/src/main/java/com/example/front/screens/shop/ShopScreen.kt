package com.example.front.screens.shop

import ToastHost
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.front.R
import com.example.front.components.ButtonWithIcon
import com.example.front.components.CardButton
import com.example.front.components.CommentsTextBox
import com.example.front.components.FilterDialogProducts
import com.example.front.components.ImageItemForProfilePic
import com.example.front.components.MyDropdownCategories
import com.example.front.components.MyDropdownMetrics
import com.example.front.components.MyDropdownSizes
import com.example.front.components.MyNumberField
import com.example.front.components.MyTextFieldWithoutIcon
import com.example.front.components.ReviewCard
import com.example.front.components.SearchTextField
import com.example.front.components.ShopProductCard
import com.example.front.components.Sidebar
import com.example.front.components.SmallElipseAndTitle
import com.example.front.components.SortDialog
import com.example.front.components.ToggleImageButtonFunction
import com.example.front.model.DTO.CategoriesDTO
import com.example.front.model.DTO.EditShopDTO
import com.example.front.model.DTO.MetricsDTO
import com.example.front.model.DTO.NewProductDTO
import com.example.front.model.DTO.NewProductDisplayDTO
import com.example.front.model.DTO.Size
import com.example.front.model.DTO.WorkingHoursNewShopDTO
import com.example.front.navigation.Screen
import com.example.front.screens.myshop.ChangeTime
import com.example.front.screens.myshop.getMultipartBodyPart
import com.example.front.screens.sellers.FilterCard
import com.example.front.ui.theme.Typography
import com.example.front.viewmodels.oneshop.OneShopViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import rememberToastHostState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavHostController,
    shopViewModel: OneShopViewModel,
    shopId: Int,
    info: Int
) {
    var id by remember { mutableStateOf(0) }
    val toastHostState = rememberToastHostState()
    LaunchedEffect(Unit) {
        shopViewModel.getUserId()
            ?.let {
                shopViewModel.getShopDetails(it, shopId)
                id = it
            }
        shopViewModel.getUserId()
            ?.let {
                shopViewModel.getProducts(
                    it,
                    listOf(),
                    null,
                    null,
                    null,
                    null,
                    0,
                    null,
                    1,
                    shopId,
                    false,
                    null,
                    null
                )
            }
        shopViewModel.getShopReview(shopId, 0)
        shopViewModel.getCategories()
        shopViewModel.getMetrics()
    }

    LaunchedEffect(shopViewModel.stateNewProductDisplay.value)
    {
        shopViewModel.getShopDetails(id, shopId)

        if (shopViewModel.state.value.shop != null) {
            if (shopViewModel.state.value.shop!!.productDisplayId != null) {
                shopViewModel.state.value.shop!!.productDisplayId?.let {
                    shopViewModel.getProductDisplay(
                        it
                    )
                }
            }

        }
    }
    LaunchedEffect(shopViewModel.stateEditShop.value)
    {
        if(shopViewModel.stateEditShop.value.error.isNotEmpty())
        {
            toastHostState.showToast(
                "Error. Please, check all fields!",
                Icons.Default.Clear
            )
        }
        else if(shopViewModel.stateEditShop.value.success != null)
        {
            shopViewModel.getUserId()
                ?.let {
                    shopViewModel.getShopDetails(it, shopId)
                    id = it
                }
        }
    }
    val context = LocalContext.current

    LaunchedEffect(shopViewModel.statedeleteShop.value) {
        val state = shopViewModel.statedeleteShop.value

        if (state?.success != null) {
            if (state.success!!.success != null) {
                navController.navigate(route = Screen.Home.route)
            }
        } else if (state.error.contains("NotFound")) {
            try {
                toastHostState.showToast(state.error, Icons.Default.Clear)
            } catch (e: Exception) {
                Log.e("ToastError", "Error showing toast", e)
            }
        } else if (state.error.contains("You cannot delete the store now! Try it later.")) {
            try {
                toastHostState.showToast(state.error, Icons.Default.Clear)
            } catch (e: Exception) {
                Log.e("ToastError", "Error showing toast", e)
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Sidebar(
        drawerState,
        navController,
        shopViewModel.dataStoreManager
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        )
        {
            item {
                SmallElipseAndTitle(title = "Shop", drawerState)
            }
            if (shopViewModel.state.value.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    )
                    {
                        CircularProgressIndicator()
                    }
                }
            } else {
                if (shopViewModel.state.value.shop!!.productDisplayId != null) {
                    shopViewModel.state.value.shop!!.productDisplayId?.let {
                        shopViewModel.getProductDisplay(
                            it
                        )
                    }
                }
                item {
                    ProfilePic(shopViewModel, id, shopId, navController)
                }
                item {
                    ShopInfo(shopViewModel, shopId, id, navController, info)
                }
            }

        }
    }
}


@Composable
fun ShopInfo(
    shopViewModel: OneShopViewModel,
    shopId: Int,
    userID: Int,
    navController: NavHostController,
    info: Int
) {
    var isImageClicked by remember { mutableStateOf(if (info == 1) true else false) }
    var shopReviewsPage by remember { mutableStateOf(0f) }

    val scaleImage1 by animateDpAsState(
        targetValue = if (isImageClicked) 1.1.dp else 1.dp,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    val scaleImage2 by animateDpAsState(
        targetValue = if (isImageClicked) 1.dp else 1.1.dp,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    val zIndexImage1 by animateDpAsState(
        targetValue = if (isImageClicked) 1.dp else 0.dp,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    val zIndexImage2 by animateDpAsState(
        targetValue = if (isImageClicked) 0.dp else 1.dp,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.jakoprovidnaelipsa),
                contentDescription = "Elipse",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(zIndexImage1.value)
                    .graphicsLayer(scaleX = scaleImage1.value, scaleY = scaleImage1.value)
            )
            Image(
                painter = painterResource(id = R.drawable.jakoprovidnaelipsa2),
                contentDescription = "Elipse",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(zIndexImage2.value)
                    .graphicsLayer(scaleX = scaleImage2.value, scaleY = scaleImage2.value)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, top = 15.dp, end = 16.dp)
                    .zIndex(2f)
            )
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(2f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Info",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = if (isImageClicked) 25.sp else 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.clickable {
                            isImageClicked = true
                        }
                    )
                    Text(
                        "Products",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = if (!isImageClicked) 25.sp else 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.clickable {
                            isImageClicked = false
                        }
                    )
                }

                Info(isImageClicked, shopViewModel, shopId, userID, LocalContext.current)
                Products(isImageClicked, shopViewModel, shopId, navController)
            }
        }
    }
}

@Composable
fun Products(
    isImageClicked: Boolean,
    shopViewModel: OneShopViewModel,
    shopId: Int,
    navController: NavHostController
) {
    var showElseText by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showAddProduct by remember { mutableStateOf(false) }
    var showSortDialog by remember {
        mutableStateOf(false)
    }

    var value by remember {
        mutableStateOf("")
    }

    val data = listOf(
        1, 2, 3, 4
    )

    LaunchedEffect(!isImageClicked) {
        delay(200)
        showElseText = true
    }
    if (showElseText && !isImageClicked) {
        Column {
            if (shopViewModel.state.value.shop!!.isOwner) {
                Column(
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    ButtonWithIcon(
                        text = "Add product",
                        onClick = { showAddProduct = true },
                        width = 0.6f,
                        color = MaterialTheme.colorScheme.primary,
                        imagePainter = painterResource(
                            id = R.drawable.plus,
                        ),
                        height = 40
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )
            {
                SearchTextField(
                    valuee = value,
                    placeh = "Search products",
                    onValueChangee = { value = it; shopViewModel.Search(value, shopId) },
                    modifier = Modifier.fillMaxWidth(0.65f)
                )
                Image(
                    painter = painterResource(id = R.drawable.filters),
                    contentDescription = "Placeholder",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 10.dp)
                        .clickable {
                            showDialog = true
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.sort),
                    contentDescription = "Placeholder",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 10.dp)
                        .clickable { showSortDialog = true }
                )
            }
            if (shopViewModel.stateProduct.value.isLoading) {
                CircularProgressIndicator()
            } else if (shopViewModel.stateProduct.value.error.contains("NotFound")) {
                androidx.compose.material3.Text(
                    "No products found",
                    style = MaterialTheme.typography.titleSmall, modifier = Modifier
                        .padding(top = 30.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 25.dp)
                        .heightIn(400.dp, 600.dp)
                ) {
                    shopViewModel.stateProduct.value.products?.let { products ->
                        items(products) { product ->
                            val id = product.id
                            ShopProductCard(
                                imageRes = product.image,
                                text = product.name,
                                price = "${product.price} din/kom",
                                onClick = {
                                    navController.navigate("${Screen.Product.route}/$id")
                                }
                            )
                        }
                    }
                }
            }

        }
    }
    if (showDialog) {
        FilterDialogProducts(onDismiss = { showDialog = false }, shopViewModel, shopId)
    }
    if (showSortDialog) {
        SortDialog(onDismiss = { showSortDialog = false }, shopViewModel, shopId)
    }
    if (showAddProduct) {
        AddProductDialog(onDismiss = { showAddProduct = false }, shopViewModel, shopId, navController)
    }
}


@Composable
fun AddProductDialog(onDismiss: () -> Unit, shopViewModel: OneShopViewModel, shopId: Int?, navController: NavHostController) {
    var currentStep by remember { mutableStateOf(0) }

    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)

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
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->

                        }
                    }
                    .padding(top = 5.dp)
                    .align(Alignment.Center),
            ) {
                when (currentStep) {
                    0 -> {
                        contentOfAddNewProduct(shopViewModel, shopId!!, onNextClicked = { currentStep = 1 })
                    }

                    1 -> {
                        contentOfAddNewImage(shopViewModel, navController,shopId!!, onNextClicked = { currentStep = 0 }, )
                    }
                }
            }
        }
    }
}


@Composable
fun contentOfAddNewImage(shopViewModel: OneShopViewModel, navController: NavHostController,shopId: Int, onNextClicked: () -> Unit) {
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris?.let { selectedUris ->
            shopViewModel.processSelectedImages(selectedUris, context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Add images for product",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.upload_image),
            contentDescription = "Upload Image Placeholder",
            modifier = Modifier
                .size(150.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { photoPicker.launch("image/*") },
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { shopViewModel.uploadAllImages() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Text("Upload Images", color = MaterialTheme.colorScheme.onSecondary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onNextClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Go Back")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.navigate("${Screen.MyShop.route}/$shopId") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Finish")
        }
    }
}





@Composable
fun contentOfAddNewProduct(shopViewModel: OneShopViewModel, shopId: Int, onNextClicked: () -> Unit) {

    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var selectedMetric by remember { mutableStateOf(MetricsDTO(1, "Piece")) }
    var selectedCategory by remember { mutableStateOf(CategoriesDTO(1, "Food")) }
    var price by remember { mutableStateOf(0f) }
    var salePercentage by remember { mutableStateOf(0f) }
    var saleMinQuantity by remember { mutableStateOf(0) }
    var saleMessage by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf(0f) }
    val sizeOptions = createSizeList()
    var selectedSize by remember { mutableStateOf<Pair<Int, String>?>(Pair(0, "None - No size (eg. Chair)")) }
    var quantity by remember { mutableStateOf(0) }
    var sizes by remember { mutableStateOf(listOf<Size>()) }

    val toastHostState = rememberToastHostState()
    val coroutineScope = rememberCoroutineScope()



    Column {
        if (shopViewModel.stateGetCategories.value.isLoading && shopViewModel.stateGetMetrics.value.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Add new product",
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                        )
                    }
                }
                item {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 10.dp)) {
                        MyTextFieldWithoutIcon(
                            labelValue = "Product name",
                            value = productName,
                            onValueChange = {
                                productName = it
                            },
                            modifier = Modifier
                        )
                    }
                }
                item {
                    CommentsTextBox(
                        onReviewTextChanged = { newText ->
                            productDescription = newText
                        },
                        placeholder = "Product Description"
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        MyTextFieldWithoutIcon(
                            labelValue = "Unit price",
                            value = price.toString(),
                            onValueChange = {
                                price = it.toFloat()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 2.dp)
                        )
                        shopViewModel.stateGetMetrics.value.metrics?.let {
                            MyDropdownMetrics(
                                labelValue = "Metric",
                                selectedMetric = selectedMetric,
                                metricsList = it,
                                onMetricSelected = { newMetric ->
                                    selectedMetric = newMetric
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp)
                            )
                        }
                    }
                }
                item{

                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)) {
                        MyDropdownSizes(
                            labelValue = "Select Size",
                            selectedSize = selectedSize,
                            sizesList = sizeOptions,
                            onSizeSelected = { sizePair ->
                                selectedSize = sizePair
                            },
                            isEnabled = selectedMetric.id == 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        NumberPicker(
                            labelValue = "Quantity",
                            value = quantity,
                            onValueChange = { newQuantity -> quantity = newQuantity },
                            enabled = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                selectedSize?.let { size ->
                                    val existingIndex = sizes.indexOfFirst { it.sizeId == size.first }
                                    if (existingIndex != -1) {
                                        val existingSize = sizes[existingIndex]
                                        val updatedSize = existingSize.copy(quantity = existingSize.quantity + quantity)
                                        sizes = sizes.toMutableList().apply { set(existingIndex, updatedSize) }
                                    } else {
                                        sizes = sizes + Size(size.first, quantity)
                                    }
                                    quantity = 0
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true
                        ) {
                            Text("Add Size", style = MaterialTheme.typography.bodyMedium,color = Color.White)
                        }

                        sizes.forEachIndexed { index, size ->
                            Row(
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Text(
                                    "Size: ${sizeOptions.firstOrNull { it.first == size.sizeId }?.second}, Quantity: ${size.quantity}",
                                    style = Typography.bodyLarge,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                )

                                IconButton(
                                    onClick = {
                                        if (size.quantity > 0) {
                                            sizes = sizes.toMutableList().also {
                                                it[index] = it[index].copy(quantity = it[index].quantity - 1)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Decrease Quantity")
                                }

                                IconButton(
                                    onClick = {
                                        sizes = sizes.toMutableList().also {
                                            it[index] = it[index].copy(quantity = it[index].quantity + 1)
                                        }
                                    }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Increase Quantity")
                                }

                                IconButton(
                                    onClick = {
                                        sizes = sizes.toMutableList().apply { removeAt(index) }
                                    }
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        MyTextFieldWithoutIcon(
                            labelValue = "Weight of single piece (kg)",
                            value = weight.toString(),
                            onValueChange = {
                                weight = it.toFloat()
                            },
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
                item {
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        Row() {
                            MyTextFieldWithoutIcon(
                                labelValue = "Discount",
                                value = salePercentage.toString(),
                                onValueChange = {
                                    salePercentage = it.toFloat()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp)
                            )
                            MyTextFieldWithoutIcon(
                                labelValue = "If quantity over ...",
                                value = saleMinQuantity.toString(),
                                onValueChange = {
                                    saleMinQuantity = it.toInt()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp)
                            )
                        }
                        Row() {
                            MyTextFieldWithoutIcon(
                                labelValue = "Discount message",
                                value = saleMessage,
                                onValueChange = {
                                    saleMessage = it
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp)
                            )
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.padding(start = 16.dp, end = 10.dp)) {
                        shopViewModel.stateGetCategories.value.categories?.let {
                            MyDropdownCategories(
                                labelValue = "Category",
                                selectedCategory = selectedCategory,
                                categoriesList = it,
                                onCategorySelected = { newCat ->
                                    selectedCategory = newCat
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            val errorMess = when {
                                productName?.isBlank() == true -> "Product name is required"
                                productDescription?.isBlank() == true -> "Product description is required"
                                price == null || price == 0f -> "Valid price is required"
                                salePercentage == null -> "Valid sale percentage is required"
                                saleMinQuantity == null -> "Valid sale minimum quantity is required"
                                saleMessage?.isBlank() == true -> "Sale message is required"
                                selectedCategory.id == 0 -> "Category ID is required"
                                shopId == null || shopId == 0 -> "Shop ID is required"
                                weight == null || weight == 0f -> "Valid weight is required"
                                sizes.isEmpty() -> "At least one size is required"
                                else -> null
                            }

                            if (errorMess != null) {
                                coroutineScope.launch {
                                    try {
                                        toastHostState.showToast(errorMess, Icons.Default.Clear)
                                    } catch (e: Exception) {
                                        Log.e("ToastError", "Error showing toast", e)
                                    }
                                }
                            } else {
                                val newProductDTO = NewProductDTO(
                                    name = productName,
                                    description = productDescription,
                                    metricId = selectedMetric.id,
                                    price = price,
                                    salePercentage = salePercentage,
                                    saleMinQuantity = saleMinQuantity.toInt(),
                                    saleMessage = saleMessage,
                                    categoryId = selectedCategory.id,
                                    shopId = shopId,
                                    weight = weight,
                                    sizes = sizes.toList()
                                )
                                shopViewModel.postNewProduct(newProductDTO)
                                onNextClicked()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Proceed", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }
            }
        }
    }
    ToastHost(hostState = toastHostState)
}


@Composable
fun NumberPicker(
    labelValue: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(
            onClick = { if (value > minValue) onValueChange(value - 1) },
            enabled = enabled && value > minValue
        ) {
            Text("-", color = Color.White)
        }

        Text(
            text = "$labelValue: $value",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(16.dp),
            color = Color.Black
        )

        Button(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            enabled = enabled && value < maxValue
        ) {
            Text("+", color = Color.White)
        }
    }
}

data class CategoriesDTO(val id: Int, val name: String)


fun createSizeList(): List<Pair<Int, String>> {
    return listOf(
        0 to "None - No size (eg. Chair)",
        1 to "XS",
        2 to "S",
        3 to "M",
        4 to "L",
        5 to "XL",
        6 to "XXL",
        7 to "35",
        8 to "36",
        9 to "37",
        10 to "38",
        11 to "39",
        12 to "40",
        13 to "41",
        14 to "42",
        15 to "43",
        16 to "44",
        17 to "45",
        18 to "46",
        19 to "47"
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Info(
    isImageClicked: Boolean,
    shopViewModel: OneShopViewModel,
    shopId: Int,
    userID: Int,
    context: Context
) {
    val state = shopViewModel.state.value.shop
    var showText by remember { mutableStateOf(false) }
    var firstTime by remember { mutableStateOf(true) }
    var showReviews by remember { mutableStateOf(false) }
    var leaveAReview by remember { mutableStateOf(false) }
    var reviewPage by remember { mutableStateOf(0) }
    var comment by remember {
        mutableStateOf("")
    }
    var selectedRating by remember { mutableStateOf(0) }
    var toast by remember { mutableStateOf(false) }
    val toastHostState = rememberToastHostState()
    val coroutineScope = rememberCoroutineScope()
    var feedback by remember { mutableStateOf(false) }


    if (isImageClicked) {
        var showText by remember { mutableStateOf(false) }

        LaunchedEffect(isImageClicked) {
            delay(200)
            showText = true
            firstTime = false
        }
        if (showText || firstTime) {

            Column(
                modifier = Modifier
                    .padding(top = 50.dp, start = 5.dp)
                    .zIndex(2f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // -- SHOP CATEGORIES
                    Image(
                        painter = painterResource(id = R.drawable.categories),
                        contentDescription = "Placeholder",
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Text(
                        text = "Shop categories",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                }
                Text(
                    text = state!!.categories.joinToString(", "),
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                )


                // -- SUBCATEGORIES --
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.subcategories),
                        contentDescription = "Placeholder",
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Text(
                        text = "Shop subcategories",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )

                }

                if (state!!.subcategories!!.isEmpty()) {
                    Text(
                        text = "-",
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                } else {
                    Text(
                        text = state!!.subcategories!!.joinToString(", "),
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }


                // -- PICK UP TIME --
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "Placeholder",
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Text(
                        text = "Pick up time",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                }

                val workingHours = shopViewModel.state.value.shop!!.workingHours

                if (workingHours != null) {
                    for (workingHour in workingHours) {
                        val dayOfWeek = when (workingHour.day) {
                            1 -> "Mon"
                            2 -> "Tue"
                            3 -> "Wen"
                            4 -> "Tru"
                            5 -> "Fri"
                            6 -> "Sut"
                            7 -> "Sun"
                            else -> {
                                "-"
                            }
                        }
                        val openingHours = workingHour.openingHours
                        val closingHours = workingHour.closingHours
                        Text(
                            text = "$dayOfWeek: $openingHours - $closingHours",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }

                // -- REVIEWS --
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Search icon",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Reviews",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                }
                if (!shopViewModel.state.value.shop!!.isOwner) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = "Leave a review",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable {
                                    leaveAReview = !leaveAReview
                                },
                            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                        Icon(
                            imageVector = if (leaveAReview) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    leaveAReview = !leaveAReview
                                }
                                .padding(top = 8.dp)
                                .size(30.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (leaveAReview) {
                        if (shopViewModel.state.value.shop!!.rated || feedback) {
                            Text(
                                "A review from you has already been submitted.",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 20.dp)
                            )
                        }
                        else if(shopViewModel.state.value.shop!!.boughtFrom == false)
                        {
                            Text(
                                "You must buy something in order to leave a review!",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 20.dp),
                                textAlign = TextAlign.Center
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
                                    shopViewModel.leaveReview(
                                        shopId,
                                        userID,
                                        selectedRating,
                                        comment
                                    )
                                    toast = true
                                }, 0.9f,
                                Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(50.dp), MaterialTheme.colorScheme.secondary
                            )

                            if (!shopViewModel.statePostReview.value.isLoading && toast) {
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
                                shopViewModel.getShopReview(
                                    shopId,
                                    reviewPage
                                )
                                toast = false
                            } else if (shopViewModel.statePostReview.value.error.isNotEmpty() && toast) {
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
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                    Icon(
                        imageVector = if (showReviews) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                showReviews = !showReviews
                            }
                            .padding(top = 8.dp)
                            .size(30.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (showReviews) {
                    val rev = shopViewModel.stateReview.value.reviews
                    if (rev.isEmpty()) {
                        Text(
                            "No reviews found for this store.",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp)
                        )
                    }
                    LazyColumn(
                        modifier = Modifier.heightIn(100.dp, 1000.dp)
                    ) {
                        items(rev) { review ->
                            ReviewCard(
                                username = review.username,
                                imageRes = review.image,
                                comment = review.comment,
                                rating = review.rating
                            )
                        }
                    }
                    if (!shopViewModel.stateReview.value.error.isNotEmpty()) {
                        Text("See more",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .clickable {
                                    reviewPage++; shopViewModel.getShopReview(
                                    shopId,
                                    reviewPage
                                )
                                }
                                .align(Alignment.CenterHorizontally))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfilePic(shopViewModel: OneShopViewModel, id: Int, shopId: Int, navController: NavHostController) {
    val state = shopViewModel.state.value
    var showDialog by remember {
        mutableStateOf(false)
    }
    var showDisplayProduct by remember {
        mutableStateOf(false)
    }
    var showProductDisplayNotificationDialog by remember {
        mutableStateOf(false)
    }
    var showDeleteShopDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .padding(top = 50.dp, end = 16.dp, start = 16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    )
    {
        if (!shopViewModel.state.value.shop!!.isOwner) {
            ToggleImageButtonFunction(modifier = Modifier.align(Alignment.TopEnd), onClick = {
                shopViewModel.changeToggleLike(id, shopViewModel.state.value.shop!!.id)
            }, shopViewModel.state.value.shop!!.liked)
        } else {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Search icon",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        showDialog = true
                    },
                tint = MaterialTheme.colorScheme.onBackground
            )
        }


        if (shopViewModel.state.value.shop!!.isOwner) {
            Image(
                painter = painterResource(id = R.drawable.addshop),
                contentDescription = "Search icon",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopStart)
                    .clickable {
                        showDisplayProduct = true
                    },
            )
        } else {
            val chatId = "0"
            val image = shopViewModel.state.value.shop!!.image
            Icon(
                painter = painterResource(id = R.drawable.navbar_message),
                contentDescription = "Search icon",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopStart)
                    .clickable {
                        val user2Id = shopViewModel.state.value.shop!!.ownerId
                        navController.navigate("${Screen.Chat.route}/$chatId/$user2Id/$image")
                    },
                tint = MaterialTheme.colorScheme.tertiary
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
            ) {
                if (shopViewModel.state.value.shop?.image == null) {
                    Image(
                        painter = painterResource(id = R.drawable.imageplaceholder),
                        contentDescription = "Placeholder",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                    )
                } else {
                    val image = shopViewModel.state.value.shop!!.image
                    val imageUrl = "http://softeng.pmf.kg.ac.rs:10015/images/${image}"

                    val painter: Painter = rememberAsyncImagePainter(model = imageUrl)
                    Image(
                        painter = painter,
                        contentDescription = "Placeholder",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                    )
                }

                if (shopViewModel.stateProductDisplay.value.displayProduct != null) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .align(Alignment.TopEnd)
                            .clickable {
                                showProductDisplayNotificationDialog = true
                            }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.shop!!.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.shop!!.address,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    RatingBar(rating = shopViewModel.state.value.shop!!.rating!!.toFloat())
                    if (shopViewModel.state.value.shop!!.isOwner) {
                        CardButton(
                            text = "Delete this shop",
                            onClick = { showDeleteShopDialog = true },
                            width = 0.45f,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    if (showDialog) {
        EditSellersDialog(onDismiss = { showDialog = false },shopViewModel)
    }

    if (showDisplayProduct) {
        shopViewModel.inicijalnoStanjeNewPD();
        DisplayProductDialog(
            onDismiss = { showDisplayProduct = false },
            shopViewModel,
            shopId,
            id
        )
    }

    if (showProductDisplayNotificationDialog) {
        ProductDisplayNotification(
            onDismiss = { showProductDisplayNotificationDialog = false },
            shopViewModel,
            id,
            shopId
        )
    }
    if (showDeleteShopDialog) {
        DeleteShopDialog(onDismiss = { showDeleteShopDialog = false }, shopViewModel, shopId)
    }
}

@Composable
fun DeleteShopDialog(onDismiss: () -> Unit, shopViewModel: OneShopViewModel, shopId: Int) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    var deletedialog by remember {
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
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                Column {
                    Text(
                        "Are you sure you want to delete your store?",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .padding(bottom = 25.dp),
                        textAlign = TextAlign.Center
                    )
                    Row()
                    {
                        CardButton(
                            text = "No",
                            onClick = { onDismiss() },
                            width = 0.5f,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        CardButton(
                            text = "Yes",
                            onClick = {
                                shopViewModel.deleteShop(shopId)
                                onDismiss()
                            },
                            width = 0.95f,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDisplayNotification(
    onDismiss: () -> Unit,
    shopViewModel: OneShopViewModel,
    id: Int,
    shopId: Int
) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    var deletedialog by remember {
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
                    .fillMaxHeight(0.25f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->

                        }
                    }
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                if (shopViewModel.stateProductDisplay.value.isLoading) {
                    CircularProgressIndicator()
                } else {
                    val state = shopViewModel.stateProductDisplay.value
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(bottom = 25.dp)
                                .fillMaxWidth()
                        ) {
//                            if(shopViewModel.state.value.shop!!.isOwner)
//                            {
//                                Icon(
//                                    imageVector = Icons.Default.Edit,
//                                    contentDescription = "Delete Icon",
//                                    tint = MaterialTheme.colorScheme.primary,
//                                    modifier = Modifier
//                                        .clickable { }
//                                        .size(30.dp)
//                                )
//                            }
                            Text(
                                text = "Product display",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier//.padding(bottom = 25.dp)
                            )
                            if (shopViewModel.state.value.shop!!.isOwner) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable {
                                            deletedialog = true
                                        }
                                        .size(30.dp)
                                )
                            }

                        }
                        if (state != null) {
                            Text(
                                state.displayProduct!!.startDate + " - " + state.displayProduct!!.endDate,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                state.displayProduct!!.startTime + " - " + state.displayProduct!!.endTime,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                state.displayProduct!!.address,
                                style = MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (deletedialog) {
        DeleteDialog(
            text = "Do you want to delete the product display?",
            onDismiss = { deletedialog = false },
            onYesClick = { deletedialog = false; onDismiss() },
            shopViewModel = shopViewModel,
            id,
            shopId
        )
    }
}

@Composable
fun DeleteDialog(
    text: String,
    onDismiss: () -> Unit,
    onYesClick: () -> Unit,
    shopViewModel: OneShopViewModel,
    id: Int,
    shopId: Int
) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)

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
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->

                        }
                    }
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                Column {
                    Text(
                        text, style = MaterialTheme.typography.titleSmall, modifier = Modifier
                            .padding(bottom = 25.dp), textAlign = TextAlign.Center
                    )
                    Row()
                    {
                        CardButton(
                            text = "No",
                            onClick = { onDismiss() },
                            width = 0.5f,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        CardButton(
                            text = "Yes",
                            onClick = {
                                shopViewModel.deleteProductDisplay(shopViewModel.state.value.shop!!.productDisplayId)
                                shopViewModel.getShopDetails(id, shopId)
                                onYesClick()
                            },
                            width = 0.95f,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayProductDialog(
    onDismiss: () -> Unit,
    shopViewModel: OneShopViewModel,
    shopId: Int,
    id: Int
) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    val state = rememberDateRangePickerState()
    val timeStart = rememberTimePickerState()
    val timeEnd = rememberTimePickerState()
    var value by remember {
        mutableStateOf("")
    }
    var showDeleteDialog by remember {
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
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->

                        }
                    }
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                LazyColumn {
                    item {
                        Text(
                            "Add product display information",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(bottom = 25.dp)
                        )

                        MyTextFieldWithoutIcon(
                            labelValue = "Address",
                            value = value,
                            onValueChange = { value = it },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Card(

                        )
                        {
                            Box(
                                modifier = Modifier
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.tertiary,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clip(RoundedCornerShape(10.dp))
                            ) {
                                DateRangePicker(
                                    state = state,
                                    modifier = Modifier
                                        .height(470.dp)
                                        .background(color = MaterialTheme.colorScheme.background),
                                    headline = {
                                        Text(
                                            text = "Select date!",
                                            modifier = Modifier.padding(start = 10.dp),
                                            style = MaterialTheme.typography.displaySmall.copy(
                                                fontSize = 23.sp
                                            )
                                        )
                                    },
                                    title = null
                                )
                            }
                        }
                        Text(
                            text = "Start time",
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 10.dp,
                                bottom = 10.dp
                            ),
                            style = MaterialTheme.typography.displaySmall
                        )
                        TimeInput(state = timeStart)
                        Text(
                            text = "End time",
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 10.dp,
                                bottom = 10.dp
                            ),
                            style = MaterialTheme.typography.displaySmall
                        )
                        TimeInput(state = timeEnd)
                        CardButton(
                            text = "Add",
                            onClick = {
                                if (shopViewModel.state.value.shop!!.productDisplayId != null && shopViewModel.state.value.shop!!.productDisplayId != 0) {
                                    showDeleteDialog = true;
                                } else {
                                    val newPD = NewProductDisplayDTO(
                                        shopId = shopId,
                                        startDate = SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                            Locale.getDefault()
                                        )
                                            .format(state.selectedStartDateMillis?.let { Date(it) }),
                                        endDate = SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                            Locale.getDefault()
                                        )
                                            .format(state.selectedEndDateMillis?.let { Date(it) }),
                                        startTime = SimpleDateFormat(
                                            "HH:mm",
                                            Locale.getDefault()
                                        )
                                            .format(Date().apply {
                                                hours = timeStart.hour; minutes =
                                                timeStart.minute
                                            }),
                                        endTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                                            .format(Date().apply {
                                                hours = timeEnd.hour; minutes = timeEnd.minute
                                            }),
                                        address = value
                                    )
                                    shopViewModel.newProductDisplay(newPD);

                                    onDismiss()
                                }
                            },
                            width = 1f,
                            modifier = Modifier.height(50.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (shopViewModel.stateNewProductDisplay.value.error.isNotEmpty()) {
                            Text(
                                "Please check all fields!",
                                style = MaterialTheme.typography.displaySmall,
                                modifier = Modifier
                            )
                        }
                        if (shopViewModel.stateNewProductDisplay.value.isLoading == false) {
                            shopViewModel.getShopDetails(id, shopId);
                            onDismiss();
                        }
                    }
                }

            }
        }
    }

    if (showDeleteDialog) {
        DeleteDialog(
            text = "If you want to add a new product display, you will have to delete the old one. Do you want to delete the old one?",
            onDismiss = { showDeleteDialog = false; shopViewModel.getShopDetails(id, shopId); },
            onYesClick = {
                showDeleteDialog = false;
                val newPD = NewProductDisplayDTO(
                    shopId = shopId,
                    startDate = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        Locale.getDefault()
                    )
                        .format(state.selectedStartDateMillis?.let { Date(it) }),
                    endDate = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        Locale.getDefault()
                    )
                        .format(state.selectedEndDateMillis?.let { Date(it) }),
                    startTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date().apply {
                            hours = timeStart.hour; minutes = timeStart.minute
                        }),
                    endTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date().apply {
                            hours = timeEnd.hour; minutes = timeEnd.minute
                        }),
                    address = value
                )
                shopViewModel.newProductDisplay(newPD);
                onDismiss()
            },
            shopViewModel = shopViewModel,
            id = id,
            shopId = shopId
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSellersDialog(onDismiss: () -> Unit, shopViewModel: OneShopViewModel) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    val state = rememberTimePickerState()

    var name by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    var pib by remember {
        mutableStateOf("")
    }
    var accountNumber by remember {
        mutableStateOf("")
    }
    var categories = mutableListOf<Int>()
    val cardData = listOf(
        "Food", "Drink", "Tools", "Clothes", "Jewerly", "Footwear", "Furniture", "Pottery", "Beauty", "Decor", "Health", "Other"
    )
    name = shopViewModel.state.value.shop!!.name
    address = shopViewModel.state.value.shop!!.address
    pib = shopViewModel.state.value.shop!!.pib.toString()
    accountNumber = if(shopViewModel.state.value.shop!!.accountNumber.toString() != null)shopViewModel.state.value.shop!!.accountNumber.toString() else ""
    var brojevi = (1..12).map { it }
    var clickedCards by remember {
        mutableStateOf(emptyList<Int>())
    }
    val cat = shopViewModel.state.value.shop!!.categories
    var kombinovanaLista = cardData.zip(brojevi)

    var selectedDay by remember { mutableStateOf<String?>("Mon") }
    var workingHoursMap by remember {
        mutableStateOf(
            mutableMapOf<Int, WorkingHoursNewShopDTO?>().apply {
                for (day in 1..7) {
                    put(day, null)
                }
            }
        )
    }

    fun updateWorkingHours(day: Int, newWorkingHours: WorkingHoursNewShopDTO?) {
        workingHoursMap = workingHoursMap.toMutableMap().apply {
            put(day, newWorkingHours)
        }
    }
    fun resetWorkingHours(day: Int) {
        workingHoursMap = workingHoursMap.toMutableMap().apply {
            put(day, null)
        }
    }

    if(shopViewModel.state.value.shop!!.workingHours != null)
    {
        for(wh in shopViewModel.state.value.shop!!.workingHours!!)
        {
            val new = WorkingHoursNewShopDTO(day=wh.day, openingHours = wh.openingHours, closingHours = wh.closingHours)
            updateWorkingHours(wh.day, newWorkingHours = new)
        }
    }



    var stateMon = rememberTimePickerState()
    var stateEndMon = rememberTimePickerState()
    if(workingHoursMap[1] != null)
    {
        var wh = workingHoursMap[1]!!.openingHours
        var parts = wh.split(":")
        var hourPart = parts[0]
        var minutePart = parts[1]
        stateMon = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
        wh = workingHoursMap[1]!!.closingHours
        parts = wh.split(":")
        hourPart = parts[0]
        minutePart = parts[1]
        stateEndMon = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
    }

    var stateTue = rememberTimePickerState()
    var stateEndTue = rememberTimePickerState()
    if(workingHoursMap[2] != null)
    {
        var wh = workingHoursMap[2]!!.openingHours
        var parts = wh.split(":")
        var hourPart = parts[0]
        var minutePart = parts[1]
        stateTue = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
        wh = workingHoursMap[2]!!.closingHours
        parts = wh.split(":")
        hourPart = parts[0]
        minutePart = parts[1]
        stateEndTue = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
    }

    var stateWen = rememberTimePickerState()
    var stateEndWen = rememberTimePickerState()
    if(workingHoursMap[3] != null)
    {
        var wh = workingHoursMap[3]!!.openingHours
        var parts = wh.split(":")
        var hourPart = parts[0]
        var minutePart = parts[1]
        stateWen = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
        wh = workingHoursMap[3]!!.closingHours
        parts = wh.split(":")
        hourPart = parts[0]
        minutePart = parts[1]
        stateEndWen = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
    }


    var stateFri = rememberTimePickerState()
    var stateEndFri = rememberTimePickerState()
    if(workingHoursMap[5] != null)
    {
        var wh = workingHoursMap[5]!!.openingHours
        var parts = wh.split(":")
        var hourPart = parts[0]
        var minutePart = parts[1]
        stateFri = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
        wh = workingHoursMap[5]!!.closingHours
        parts = wh.split(":")
        hourPart = parts[0]
        minutePart = parts[1]
        stateEndFri = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
    }


    var stateThu = rememberTimePickerState()
    var stateEndThu = rememberTimePickerState()
    if(workingHoursMap[4] != null)
    {
        var wh = workingHoursMap[4]!!.openingHours
        var parts = wh.split(":")
        var hourPart = parts[0]
        var minutePart = parts[1]
        stateThu = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
        wh = workingHoursMap[4]!!.closingHours
        parts = wh.split(":")
        hourPart = parts[0]
        minutePart = parts[1]
        stateEndThu = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
    }

    var stateSat = rememberTimePickerState()
    var stateEndSat = rememberTimePickerState()
    if(workingHoursMap[6] != null)
    {
        var wh = workingHoursMap[6]!!.openingHours
        var parts = wh.split(":")
        var hourPart = parts[0]
        var minutePart = parts[1]
        stateSat = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
        wh = workingHoursMap[6]!!.closingHours
        parts = wh.split(":")
        hourPart = parts[0]
        minutePart = parts[1]
        stateEndSat = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
    }


    var stateSun = rememberTimePickerState()
    var stateEndSun = rememberTimePickerState()
    if(workingHoursMap[7] != null)
    {
        var wh = workingHoursMap[7]!!.openingHours
        var parts = wh.split(":")
        var hourPart = parts[0]
        var minutePart = parts[1]
        stateSun = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
        wh = workingHoursMap[7]!!.closingHours
        parts = wh.split(":")
        hourPart = parts[0]
        minutePart = parts[1]
        stateEndSun = rememberTimePickerState(initialHour = hourPart.toInt(), initialMinute = minutePart.toInt())
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var picture by remember {
        mutableStateOf<MultipartBody.Part?>(null)
    }
    val toastHostState = rememberToastHostState()

    val context = LocalContext.current
    var firstTime by remember{
        mutableStateOf(true)
    }

    val photoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = uri
            if( selectedImageUri != null)
            {
                picture = getMultipartBodyPart(context, selectedImageUri!!)
            }
        }
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
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->

                        }
                    }
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                LazyColumn()
                {
                    item{
                        Column(
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Edit shop",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(bottom = 25.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Box(
                                modifier = Modifier
                                    .size(165.dp)
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    Image(
                                        painter = rememberImagePainter(
                                            data = selectedImageUri,
                                            builder = {
                                                crossfade(true)
                                            }
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(165.dp)
                                            .clip(CircleShape)
                                            .fillMaxSize()
                                            .clickable {
                                                photoPicker.launch("image/*")
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else if(shopViewModel.state.value.shop!!.image != null) {
                                    shopViewModel.state.value.shop!!.image?.let {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.size(165.dp)
                                        ) {
                                            ImageItemForProfilePic(image = it, onEditClick = {photoPicker.launch("image/*")})
                                        }
                                    }
                                }
                                else{
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.size(165.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.imageplaceholder),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.Center)
                        ) {

                            MyTextFieldWithoutIcon(
                                labelValue = "Name",
                                value = name,
                                onValueChange = { name = it }, modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            MyTextFieldWithoutIcon(
                                labelValue = "Address",
                                value = address,
                                onValueChange = { address = it }, modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            MyNumberField(labelValue = "Account Number", value = accountNumber, onValueChange={ accountNumber=it }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(16.dp))
                            MyNumberField(labelValue = "Pib", value = pib, onValueChange={ pib=it }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Shop Categories",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                            )
                            LazyVerticalGrid(
                                modifier = Modifier.heightIn(30.dp,500.dp),
                                columns = GridCells.Fixed(3),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                items(kombinovanaLista,key={(_, number) -> number }) { (cardText,number) ->
                                    val sadrziElement = cat.any { element ->
                                        element.contains(cardText)
                                    }
                                    if(sadrziElement && !clickedCards.contains(number) && firstTime)
                                    {
                                        clickedCards = clickedCards + number
                                    }
                                    FilterCard(cardText = cardText, onClick = {
                                        if (clickedCards.contains(number)) {
                                            clickedCards = clickedCards - number
                                        } else {
                                            clickedCards = clickedCards + number
                                        }
                                    }, sadrziElement)
                                }
                                firstTime = false
                            }

                            Text(
                                "Working hours",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(top=10.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val daysOfWeek =
                                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                var x = 0
                                for (day in daysOfWeek) {
                                    x++
                                    DayOfWeek(day = day,isSelected = day == selectedDay, onClick={selectedDay = day
                                    })
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            when (selectedDay) {
                                "Mon" -> {
                                    EditTime(stateMon, stateEndMon, onClick = {
                                        updateWorkingHours(1, WorkingHoursNewShopDTO(day = 1, openingHours = stateMon.hour.toString()+":"+stateMon.minute.toString(), closingHours = stateEndMon.hour.toString()+":"+stateEndMon.minute.toString()))
                                    }, onReset = {resetWorkingHours(1)})
                                }
                                "Tue" -> {
                                    EditTime(stateTue, stateEndTue, onClick = {
                                        updateWorkingHours(2, WorkingHoursNewShopDTO(day = 2, openingHours = stateTue.hour.toString()+":"+stateTue.minute.toString(), closingHours = stateEndTue.hour.toString()+":"+stateEndTue.minute.toString()))
                                    }, onReset = {resetWorkingHours(2)})
                                }
                                "Wen" -> {
                                    ChangeTime(stateWen, stateEndWen, onClick = {
                                        updateWorkingHours(3, WorkingHoursNewShopDTO(day = 3, openingHours = stateWen.hour.toString()+":"+stateWen.minute.toString(), closingHours = stateEndWen.hour.toString()+":"+stateEndWen.minute.toString()))
                                    }, onReset = {resetWorkingHours(3)})
                                }
                                "Thu" -> {
                                    EditTime(stateThu, stateEndThu, onClick = {
                                        updateWorkingHours(4, WorkingHoursNewShopDTO(day = 4, openingHours = stateThu.hour.toString()+":"+stateThu.minute.toString(), closingHours = stateEndThu.hour.toString()+":"+stateEndThu.minute.toString()))
                                    }, onReset = {resetWorkingHours(4)})
                                }
                                "Fri" -> {
                                    ChangeTime(stateFri, stateEndFri, onClick = {
                                        updateWorkingHours(5, WorkingHoursNewShopDTO(day = 5, openingHours = stateFri.hour.toString()+":"+stateFri.minute.toString(), closingHours = stateEndFri.hour.toString()+":"+stateEndFri.minute.toString()))
                                    }, onReset = {resetWorkingHours(5)})
                                }
                                "Sat" -> {
                                    EditTime(stateSat, stateEndSat, onClick = {
                                        updateWorkingHours(6, WorkingHoursNewShopDTO(day = 6, openingHours = stateSat.hour.toString()+":"+stateSat.minute.toString(), closingHours = stateEndSat.hour.toString()+":"+stateEndSat.minute.toString()))
                                    }, onReset = {resetWorkingHours(6)})
                                }
                                else -> {
                                    EditTime(stateSun, stateEndSun, onClick = {
                                        updateWorkingHours(7, WorkingHoursNewShopDTO(day = 7, openingHours = stateSun.hour.toString()+":"+stateSun.minute.toString(), closingHours = stateEndSun.hour.toString()+":"+stateEndSun.minute.toString()))
                                    }, onReset = {resetWorkingHours(7)})
                                }
                            }

                            CardButton(
                                text = "Edit",
                                onClick = {
                                    val workingHoursList: List<WorkingHoursNewShopDTO> = workingHoursMap.values.filterNotNull()
                                    val pibInt = pib.toIntOrNull()
                                    if(pibInt != null){
                                        val editShop = EditShopDTO(
                                            id = shopViewModel.state.value.shop!!.id,
                                            name = name,
                                            address = address,
                                            categories = clickedCards,
                                            workingHours = workingHoursList,
                                            pib = pibInt,
                                            accountNumber = accountNumber
                                        )
                                        shopViewModel.editShop(editShop)
                                        picture?.let {
                                            shopViewModel.uploadImage(2,shopViewModel.state.value.shop!!.id,
                                                it
                                            )
                                        }
                                    }

                                    onDismiss() },
                                width = 1f,
                                modifier = Modifier,
                                color = MaterialTheme.colorScheme.primary
                            )

                        }
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTime(state: TimePickerState, stateEnd : TimePickerState, onClick: () -> Unit, onReset: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .padding(0.dp)
    )
    {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary.copy(
                        alpha = 0.5f
                    )
                )
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            androidx.compose.material3.Text(
                text = "Pick up time",
                style = MaterialTheme.typography.bodyLarge
            )
            androidx.compose.material3.Text(
                text = "Opening time",
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 10.dp,
                    bottom = 10.dp
                ),
                style = MaterialTheme.typography.displaySmall
            )
            TimeInput(state = state, modifier = Modifier
                .scale(0.8f)
                .fillMaxWidth())
            androidx.compose.material3.Text(
                text = "Closing time",
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 10.dp,
                    bottom = 10.dp
                ),
                style = MaterialTheme.typography.displaySmall
            )
            TimeInput(state = stateEnd, modifier = Modifier
                .scale(0.8f)
                .fillMaxWidth())
            Row(
                modifier=Modifier.padding(start=10.dp,end=10.dp,bottom=16.dp)
            )
            {
                CardButton(
                    text = "Apply",
                    onClick = {
                        //onClick()
                    }, width = 0.5f, modifier = Modifier, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(5.dp))
                CardButton(text = "Remove", onClick = {  }, width = 0.95f, modifier = Modifier, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun DayOfWeek(day: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(5.dp)
            )
            .height(35.dp)
            .width(35.dp)
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

