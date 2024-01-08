package com.example.front.screens.chat

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.front.components.Sidebar
import com.example.front.components.SmallElipseAndTitle
import com.example.front.viewmodels.chat.ChatViewModel
import com.example.front.viewmodels.chat.Message
import com.example.front.viewmodels.myprofile.MyProfileViewModel

@Composable
fun ChatPage(
    viewModel: ChatViewModel,
    chatID: String,
    user2Id: Int,
    myProfileViewModel: MyProfileViewModel,
    shopImage: String,
    navHostController: NavHostController
) {
    val messages by viewModel.messages.collectAsState()
    val textState = remember { mutableStateOf("") }

    val (chatId, setChatId) = remember { mutableStateOf("") }
    val (user1Id, setUser1Id) = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.dataStoreManager.getUserIdFromToken()?.let { userId ->
            val currentUserID = userId.toString()
            setUser1Id(currentUserID)

            val user2IdStr = user2Id.toString()
            val calculatedChatId = if (chatID != "0") chatID.toString() else viewModel.generateChatId(currentUserID, user2IdStr)
            setChatId(calculatedChatId)

            viewModel.checkOrCreateChatSession(currentUserID, user2IdStr) {
                viewModel.listenForMessages(chatId) { newMessage ->
                    viewModel.addMessageToList(newMessage)
                }
            }
            viewModel.loadMessages(calculatedChatId) { loadedMessages ->
                viewModel.setMessages(loadedMessages)
            }
            myProfileViewModel.getMyProfileInfo(userId)
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Sidebar(drawerState, navHostController, myProfileViewModel.dataStoreManager) {
        Column(modifier = Modifier.fillMaxSize()) {
            SmallElipseAndTitle("Chat", drawerState)
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(messages) { message ->
                    MessageRow(
                        message = message,
                        isCurrentUser = isMessageFromCurrentUser(message, user1Id),
                        currentUserProfileImageUrl = myProfileViewModel.state.value.info?.image!!,
                        otherUserProfileImageUrl = shopImage
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    placeholder = { Text("Type a message") }
                )
                Button(onClick = {
                    viewModel.sendMessage(chatId, user1Id, textState.value)
                    textState.value = ""
                }) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun MessageRow(
    message: Message,
    isCurrentUser: Boolean,
    currentUserProfileImageUrl: String,
    otherUserProfileImageUrl: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isCurrentUser) {
            ProfileImage(url = otherUserProfileImageUrl)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = message.text,
            modifier = Modifier
                .background(
                    color = if (isCurrentUser) Color(0xFFDCF8C6) else Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .weight(1f) // Ensure Text takes up available space
        )
        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp)) // Added Spacer for padding
            ProfileImage(url = currentUserProfileImageUrl)
        }
    }
}



@Composable
fun ProfileImage(url: String) {
    val imageUrl = "http://softeng.pmf.kg.ac.rs:10015/images/${url}"
    val painter: Painter = rememberAsyncImagePainter(model = imageUrl)
    Image(
        painter = painter,
        contentDescription = "Profile picture",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .padding(end = 8.dp)
    )
}


fun isMessageFromCurrentUser(message: Message, currentUserId: String): Boolean {
    return message.sender == currentUserId
}