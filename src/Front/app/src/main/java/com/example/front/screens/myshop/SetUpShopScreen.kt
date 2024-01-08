package com.example.front.screens.myshop

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.front.R
import com.example.front.components.BigBlueButton
import com.example.front.components.CardButton
import com.example.front.components.MyNumberField
import com.example.front.components.MyTextField
import com.example.front.components.MyTextFieldWithoutIcon
import com.example.front.components.OpenNow
import com.example.front.components.Sidebar
import com.example.front.components.SmallElipseAndTitle
import com.example.front.model.DTO.NewShopDTO
import com.example.front.model.DTO.WorkingHoursNewShopDTO
import com.example.front.navigation.Screen
import com.example.front.screens.sellers.FilterCard
import com.example.front.screens.sellers.ReviewStars
import com.example.front.viewmodels.myshop.MyShopViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rememberToastHostState
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUpShopScreen(navController : NavHostController, viewModel: MyShopViewModel, userId:Int) {

    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Sidebar(
        drawerState,
        navController,
        viewModel.dataStoreManager
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        )
        {
            item {
                SmallElipseAndTitle(title = "Shop Setup", drawerState)
            }
            item {
                ProfilePhoto(context, userId,viewModel, navController)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePhoto(context:Context, userId:Int, viewModel: MyShopViewModel, navController: NavHostController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var day_index by remember {
        mutableStateOf(0)
    }
    var name by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    var accountNumber by remember {
        mutableStateOf("")
    }
    var pib by remember {
        mutableStateOf("")
    }
    var selectedDay by remember { mutableStateOf<String?>("Mon") }
    var firstTime by remember {
        mutableStateOf(true)
    }

    var stateMon = rememberTimePickerState()
    var stateEndMon = rememberTimePickerState()

    var stateTue = rememberTimePickerState()
    var stateEndTue = rememberTimePickerState()

    var stateWen = rememberTimePickerState()
    var stateEndWen = rememberTimePickerState()

    var stateFri = rememberTimePickerState()
    var stateEndFri = rememberTimePickerState()

    var stateThu = rememberTimePickerState()
    var stateEndThu = rememberTimePickerState()

    var stateSat = rememberTimePickerState()
    var stateEndSat = rememberTimePickerState()

    var stateSun = rememberTimePickerState()
    var stateEndSun = rememberTimePickerState()

    var workingHoursMap by remember {
        mutableStateOf(
            mutableMapOf<Int, WorkingHoursNewShopDTO?>().apply {
                for (day in 1..7) {
                    put(day, null)
                }
            }
        )
    }

    var picture by remember {
        mutableStateOf<MultipartBody.Part?>(null)
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

    var clickedCards by remember {
        mutableStateOf(emptyList<Int>())
    }

    val toastHostState = rememberToastHostState()
    val cardData = listOf(
        "Food", "Drink", "Tools", "Clothes", "Jewerly", "Footwear", "Furniture", "Pottery", "Beauty", "Decor", "Health", "Other"
    )

    var brojevi = (1..12).map { it }
    var kombinovanaLista = cardData.zip(brojevi)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // slika
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
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable {
                        photoPicker.launch("image/*")
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Upload shop profile photo",
                        modifier = Modifier.padding(5.dp, top = 8.dp),
                        style = MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.background),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        MyTextFieldWithoutIcon(labelValue = "Shop Name", value = name, onValueChange={ name=it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        MyTextFieldWithoutIcon(labelValue = "Shop Address", value = address, onValueChange={ address=it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        MyTextFieldWithoutIcon(labelValue = "Account Number", value = accountNumber, onValueChange={ accountNumber=it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        MyNumberField(labelValue = "Pib", value = pib, onValueChange={ pib=it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Shop Categories", modifier = Modifier.padding(top = 16.dp,bottom = 10.dp, start = 10.dp), style=MaterialTheme.typography.displaySmall)
        LazyVerticalGrid(
            modifier = Modifier.heightIn(30.dp,500.dp),
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(kombinovanaLista,key={(_, number) -> number }) { (cardText,number) ->
                FilterCard(cardText = cardText, onClick = {
                        if (clickedCards.contains(number)) {
                            clickedCards = clickedCards - number
                        } else {
                            clickedCards = clickedCards + number
                        }
                }, false)
            }
        }
        Text(text = "Pick up time", modifier = Modifier.padding(top = 16.dp,start = 10.dp), style=MaterialTheme.typography.displaySmall)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
            var x = 0
            for (day in daysOfWeek) {
                x++
                DayOfWeekItem(day = day,isSelected = day == selectedDay, onClick={selectedDay = day
                    day_index = x
                })
            }
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
        when (selectedDay) {
            "Mon" -> {
                ChangeTime(stateMon, stateEndMon, onClick = {
                    updateWorkingHours(1, WorkingHoursNewShopDTO(day = 1, openingHours = stateMon.hour.toString()+":"+stateMon.minute.toString(), closingHours = stateEndMon.hour.toString()+":"+stateEndMon.minute.toString()))
                }, onReset = {resetWorkingHours(1)})
            }
            "Tue" -> {
                ChangeTime(stateTue, stateEndTue, onClick = {
                    updateWorkingHours(2, WorkingHoursNewShopDTO(day = 2, openingHours = stateTue.hour.toString()+":"+stateTue.minute.toString(), closingHours = stateEndTue.hour.toString()+":"+stateEndTue.minute.toString()))
                }, onReset = {resetWorkingHours(2)})
            }
            "Wen" -> {
                ChangeTime(stateWen, stateEndWen, onClick = {
                    updateWorkingHours(3, WorkingHoursNewShopDTO(day = 3, openingHours = stateWen.hour.toString()+":"+stateWen.minute.toString(), closingHours = stateEndWen.hour.toString()+":"+stateEndWen.minute.toString()))
                }, onReset = {resetWorkingHours(3)})
            }
            "Thu" -> {
                ChangeTime(stateThu, stateEndThu, onClick = {
                    updateWorkingHours(4, WorkingHoursNewShopDTO(day = 4, openingHours = stateThu.hour.toString()+":"+stateThu.minute.toString(), closingHours = stateEndThu.hour.toString()+":"+stateEndThu.minute.toString()))
                }, onReset = {resetWorkingHours(4)})
            }
            "Fri" -> {
                ChangeTime(stateFri, stateEndFri, onClick = {
                    updateWorkingHours(5, WorkingHoursNewShopDTO(day = 5, openingHours = stateFri.hour.toString()+":"+stateFri.minute.toString(), closingHours = stateEndFri.hour.toString()+":"+stateEndFri.minute.toString()))
                }, onReset = {resetWorkingHours(5)})
            }
            "Sat" -> {
                ChangeTime(stateSat, stateEndSat, onClick = {
                    updateWorkingHours(6, WorkingHoursNewShopDTO(day = 6, openingHours = stateSat.hour.toString()+":"+stateSat.minute.toString(), closingHours = stateEndSat.hour.toString()+":"+stateEndSat.minute.toString()))
                }, onReset = {resetWorkingHours(6)})
            }
            else -> {
                ChangeTime(stateSun, stateEndSun, onClick = {
                    updateWorkingHours(7, WorkingHoursNewShopDTO(day = 7, openingHours = stateSun.hour.toString()+":"+stateSun.minute.toString(), closingHours = stateEndSun.hour.toString()+":"+stateEndSun.minute.toString()))
                }, onReset = {resetWorkingHours(7)})
            }
        }



        BigBlueButton(text = "Proceed", onClick = {
            val workingHoursList: List<WorkingHoursNewShopDTO> = workingHoursMap.values.filterNotNull()
            val pibInt = pib.toIntOrNull()
            if(pibInt != null || name != "" || address != "")
            {
                val newShop = NewShopDTO(ownerId = userId, name= name, address = address, accountNumber = accountNumber, categories = clickedCards, pib = pib.toInt(), workingHours = workingHoursList)
                viewModel.newShop(newShop, picture)
                if(viewModel.stateNewShop.value.newShop != null)
                {
                    navController.navigate("${Screen.MyShop.route}/${viewModel.stateNewShop.value.newShop!!.success}/${1}")
                }
            }
            else{
                coroutineScope.launch {
                    toastHostState.showToast(
                        "Invalid pib",
                        Icons.Default.Clear
                    )
                }
            }

        }, width = 0.9f, modifier = Modifier)
//
//        if(!viewModel.stateNewShop.value.isLoading && viewModel.stateNewShop.value.error.isEmpty() && firstTime)
//        {
//            firstTime = false
//            //val shopId = viewModel.stateNewShop.value.newShop!!.success
//            //val info = 1
//            navController.navigate("${Screen.MyShop.route}")
//        }
        if(!viewModel.stateNewShop.value.isLoading && viewModel.stateNewShop.value.error.isNotEmpty())
        {
            coroutineScope.launch {
                toastHostState.showToast(
                    "Please check all fields",
                    Icons.Default.Clear
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeTime(state: TimePickerState, stateEnd : TimePickerState,onClick: () -> Unit, onReset: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .padding(10.dp)
    )
    {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text(text="Pick up time", style=MaterialTheme.typography.bodyLarge)
            Text(text = "Opening time", modifier = Modifier.padding(top = 16.dp,start = 10.dp, bottom = 10.dp), style=MaterialTheme.typography.displaySmall)
            TimeInput(state = state)
            Text(text = "Closing time", modifier = Modifier.padding(top = 16.dp,start = 10.dp, bottom = 10.dp), style=MaterialTheme.typography.displaySmall)
            TimeInput(state = stateEnd)
            Row()
            {
                CardButton(
                    text = "Apply",
                    onClick = {
                        onClick()
                    }, width = 0.5f, modifier = Modifier, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(5.dp))
                CardButton(text = "Remove", onClick = { onReset() }, width = 0.95f, modifier = Modifier, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun DayOfWeekItem(day: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
            .height(48.dp)
            .width(48.dp)
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getRealPathFromURI(context: Context, uri: Uri): String {
    var realPath: String? = null
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = context.contentResolver.query(uri, proj, null, null, null)
    if (cursor != null) {
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        realPath = cursor.getString(columnIndex)
        cursor.close()
    }
    return realPath ?: ""
}

fun getMultipartBodyPart(context: Context, uri: Uri): MultipartBody.Part {
    val file = File(getRealPathFromURI(context, uri))
    val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData("image", file.name, requestFile)
}
