package com.example.front.screens.delivery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.front.R
import com.example.front.components.CardButton
import com.example.front.components.Sidebar
import com.example.front.components.SmallElipseAndTitle
import com.example.front.components.Tabs
import com.example.front.model.DTO.ReqForDeliveryPersonDTO
import com.example.front.model.DTO.Trip
import com.example.front.navigation.Screen
import com.example.front.ui.theme.Typography
import com.example.front.screens.checkout.ModalDatePicker
import com.example.front.viewmodels.delivery.DeliveryViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DeliveryScreen(navHostController: NavHostController, deliveryViewModel: DeliveryViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedColumnIndex by remember {
        mutableStateOf(true)
    }
    var userId by remember {
        mutableStateOf(0)
    }
    Sidebar(
        drawerState,
        navHostController,
        deliveryViewModel.dataStoreManager
    ) {
        LaunchedEffect(Unit) {
            deliveryViewModel.getRouteDetails(deliveryViewModel.dataStoreManager.getUserIdFromToken())
            deliveryViewModel.dataStoreManager.getUserIdFromToken()
                ?.let { deliveryViewModel.getReqForDelivery(it); userId=it }
        }
        val trips = deliveryViewModel.state.value.details

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            SmallElipseAndTitle("Deliveries", drawerState)

            LazyColumn() {

                item {
                    Tabs(
                        onShopsSelected = { selectedColumnIndex=true },
                        onFavoritesSelected = { selectedColumnIndex=false },
                        selectedColumnIndex = selectedColumnIndex,
                        firstTab = "Routes",
                        secondTab = "Requests",
                        isFilters = false
                    )
                }
            }

            if(selectedColumnIndex)
            {
                Column(modifier = Modifier
                    .fillMaxSize()) {
                    val startDate = remember { mutableStateOf(LocalDate.now()) }
                    val endDate = remember { mutableStateOf(LocalDate.now()) }
                    val isOpenStart = remember { mutableStateOf(false) }
                    val isOpenEnd = remember { mutableStateOf(false) }
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //datum od
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = startDate.value.format(DateTimeFormatter.ISO_DATE)
                                        ?: "",
                                    label = { Text("Start Date") },
                                    onValueChange = {},
                                    shape = RoundedCornerShape(20.dp)
                                )

                                IconButton(
                                    onClick = { isOpenStart.value = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Calendar"
                                    )
                                }
                                if (isOpenStart.value == true) {
                                    ModalDatePicker(
                                        onAccept = {
                                            isOpenStart.value = false

                                            if (it != null) { // Set the date
                                                startDate.value = Instant
                                                    .ofEpochMilli(it)
                                                    .atZone(ZoneId.of("UTC"))
                                                    .toLocalDate()
                                            }
                                        },
                                        onCancel = {
                                            isOpenStart.value = false //close dialog
                                        }
                                    )
                                }
                            }
                            //vreme


                            // Prvo polje za unos teksta
                            var adresaOd by remember { mutableStateOf(TextFieldValue()) }
                            OutlinedTextField(
                                value = adresaOd,
                                onValueChange = {
                                    adresaOd = it
                                },
                                label = { Text("Start address (street, city):") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(20.dp)
                            )

                            // Drugo polje za unos teksta
                            var adresaDo by remember { mutableStateOf(TextFieldValue()) }
                            OutlinedTextField(
                                value = adresaDo,
                                onValueChange = {
                                    adresaDo = it
                                },
                                label = { Text("End address (street, city):") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(20.dp)
                            )

                            // Dugme "Add Route"
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        deliveryViewModel.addNewRoute(
                                            startDate.value,
                                            "00:00:00",
                                            adresaOd.text,
                                            adresaDo.text,
                                            350
                                        )
                                    }
                                }
                            ) {
                                Text("Add Route")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text="My routes", style=MaterialTheme.typography.titleMedium, modifier=Modifier.padding(start=16.dp))
                    Spacer(modifier = Modifier.height(13.dp))
                    if(trips == null)
                    {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 170.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(painter = painterResource(id = R.drawable.nofound), contentDescription = null, modifier = Modifier.size(200.dp))
                            Text("No routes found", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                    else{
                        trips?.let {
                            LazyColumn {
                                items(it) { trip ->
                                    DeliveryRouteCard(trip, navHostController)
                                }
                            }
                        }
                    }
                }
            }else{
                if(!deliveryViewModel.stateReq.value.isLoading && deliveryViewModel.stateReq.value.req != null)
                {
                    val yourDataList = deliveryViewModel.stateReq.value.req

                    if(yourDataList != null)
                    {
                        LazyColumn {
                            items(yourDataList) { yourData ->
                                YourDataCard(yourData = yourData, deliveryViewModel, userId)
                            }
                        }
                    }
                }
                else{
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 170.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.nofound), contentDescription = null, modifier = Modifier.size(200.dp))
                        Text("No requests found", style = MaterialTheme.typography.titleSmall)
                    }
                }

            }

        }
    }
}

@Composable
fun YourDataCard(yourData: ReqForDeliveryPersonDTO, deliveryViewModel:DeliveryViewModel, userId :Int) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "ID: ${yourData.id}", style=MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.secondary))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Locations: ${yourData.locations}", style=MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Normal))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = yourData.startAddress+" - "+yourData.endAddress, style=MaterialTheme.typography.displaySmall.copy(color = Color.Black))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CardButton(text = "Accept", onClick = { showDialog = true }, width = 0.5f, modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.primary)
                CardButton(text = "Decline", onClick = { deliveryViewModel.declineReq(yourData.id, userId) }, width = 0.9f, modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
    if(showDialog)
    {
        AcceptDialog(onDismiss = { showDialog=false }, viewModel = deliveryViewModel, userId, yourData.id)
    }
}


@Composable
fun AcceptDialog(onDismiss: () -> Unit, viewModel: DeliveryViewModel, userId: Int, reqId:Int) {
    val overlayColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    var value by remember { mutableStateOf(0) }
    var selectedId by remember { mutableStateOf(0) }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        "Accept request", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground), modifier = Modifier
                            .padding(bottom = 40.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "Choose route", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground), modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    val deliveryInfoList = viewModel.state.value.details
                    if (deliveryInfoList != null) {
                        LazyColumn {
                            items(deliveryInfoList) { deliveryInfo ->
                                DeliveryInfoCard(deliveryInfo, selectedId) { id ->
                                    selectedId = id
                                    value = deliveryInfo.id
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if(selectedId != 0)
                    {
                        CardButton(text = "Accept", onClick = { viewModel.acceptReq(reqId,selectedId, userId);onDismiss() }, width = 1f, modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryInfoCard(deliveryInfo: Trip, selectedId: Int?, onRadioButtonClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            //.background(if (deliveryInfo.id == selectedId) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface)
            .clickable {
                onRadioButtonClick(deliveryInfo.id)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (deliveryInfo.id == selectedId) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(text = "Locations: ${deliveryInfo.locations}")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DeliveryRouteCard(trip: Trip, navHostController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navHostController.navigate("${Screen.Route.route}/${trip.id}")
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.startAddress + " - " + trip.endAddress,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Date: ${trip.createdOn}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Start time: ${trip.startTime}")
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow Icon",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}