package com.example.front.screens.product

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.front.R
import com.example.front.components.CommentsTextBox
import com.example.front.components.MyDropdownCategories
import com.example.front.components.MyDropdownMetrics
import com.example.front.components.MyDropdownSizes
import com.example.front.components.MyTextFieldWithoutIcon
import com.example.front.model.DTO.CategoriesDTO
import com.example.front.model.DTO.MetricsDTO
import com.example.front.model.DTO.NewProductDTO
import com.example.front.model.DTO.Size
import com.example.front.model.product.ProductInfo
import com.example.front.model.product.Stock
import com.example.front.navigation.Screen
import com.example.front.screens.myshop.getMultipartBodyPart
import com.example.front.screens.shop.createSizeList
import com.example.front.ui.theme.Typography
import com.example.front.viewmodels.oneshop.OneShopViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EditProduct(onDismiss: () -> Unit, shopViewModel: OneShopViewModel, productInfo: ProductInfo, navHostController: NavHostController) {
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
                        contentOfAddEditProduct(shopViewModel, productInfo ,onNextClicked = { currentStep = 1 })
                    }

                    1 -> {
                        contentOfAddNewImageOnEdit(shopViewModel, onNextClicked = { currentStep = 0 }, productInfo.productId, navHostController)
                    }
                }
            }
        }
    }
}

@Composable
fun contentOfAddEditProduct(shopViewModel: OneShopViewModel, productInfo: ProductInfo ,onNextClicked: () -> Unit) {
    var productName by remember { mutableStateOf(productInfo.name) }
    var productDescription by remember { mutableStateOf(productInfo.description) }
    var selectedMetric by remember { mutableStateOf(MetricsDTO(productInfo.metricId!!,productInfo.metric!!)) }
    var selectedCategory by remember { mutableStateOf(CategoriesDTO(productInfo.categoryId!!,productInfo.category!!)) }
    var price by remember { mutableStateOf(productInfo.price) }
    var salePercentage by remember { mutableStateOf(productInfo.salePercentage) }
    var saleMinQuantity by remember { mutableStateOf(productInfo.saleMinQuantity) }
    var saleMessage by remember { mutableStateOf(productInfo.saleMessage) }
    var shopId by remember { mutableStateOf(productInfo.shopId) }
    var weight by remember { mutableStateOf(productInfo.mass) }

    val sizeOptions = createSizeList()
    var selectedSize by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var quantity by remember { mutableStateOf(0) }
    var sizes by remember { mutableStateOf(productInfo.sizes) }


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
                            value = productName!!,
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
                            labelValue = "Unity price",
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

                        com.example.front.screens.shop.NumberPicker(
                            labelValue = "Quantity",
                            value = quantity,
                            onValueChange = { newQuantity -> quantity = newQuantity },
                            enabled = selectedMetric.id == 1,
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
                                        sizes = sizes + Stock("",size.first,quantity)
                                    }
                                    quantity = 0
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedMetric.id == 1
                        ) {
                            Text("Add Size", style = MaterialTheme.typography.bodyMedium,color = Color.White)
                        }

                        sizes.forEachIndexed { index, size ->
                            Row(
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Text(
                                    "Size: ${size.sizeId}, Quantity: ${size.quantity}",
                                    style = Typography.bodyLarge,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                )

                                IconButton(
                                    onClick = {
                                        sizes = sizes.toMutableList().apply { removeAt(index) }
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete"
                                    )
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
                            labelValue = "Weight of single piece",
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
                                    saleMinQuantity = it.toFloat()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp)
                            )
                        }
                        Row() {
                            MyTextFieldWithoutIcon(
                                labelValue = "Discount message",
                                value = saleMessage!!,
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
                            val newProductDTO = NewProductDTO(
                                name = productName!!,
                                description = productDescription!!,
                                metricId = selectedMetric.id,
                                price = price!!,
                                salePercentage = salePercentage!!,
                                saleMinQuantity = saleMinQuantity!!.toInt(),
                                saleMessage = saleMessage!!,
                                categoryId = selectedCategory.id,
                                shopId = shopId!!,
                                weight = weight!!,
                                sizes = convertStockListToSizeList(sizes.toList())
                            )
                            shopViewModel.editProduct(newProductDTO)
                            onNextClicked()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Proceed", style = MaterialTheme.typography.bodyMedium,color = Color.White)
                    }
                }
            }
        }
    }
}

fun convertStockListToSizeList(stockList: List<Stock>): List<Size> {
    return stockList.map { stock ->
        Size(sizeId = stock.sizeId, quantity = stock.quantity)
    }
}

@Composable
fun contentOfAddNewImageOnEdit(
    shopViewModel: OneShopViewModel,
    onNextClicked: () -> Unit,
    productId: Int?,
    navHostController: NavHostController){
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
            onClick = { shopViewModel.uploadAllImagesEdit(productId!!) },
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
            onClick = { navHostController.navigate("${Screen.Product.route}/$productId") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Finish")
        }
    }
}