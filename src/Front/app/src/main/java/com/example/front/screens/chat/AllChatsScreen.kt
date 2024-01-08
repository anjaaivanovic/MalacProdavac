package com.example.front.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.front.components.Sidebar
import com.example.front.components.SmallElipseAndTitle
import com.example.front.navigation.Screen
import com.example.front.viewmodels.chat.ChatViewModel

@Composable
fun ChatSessionsList(viewModel: ChatViewModel, navHostController: NavHostController) {
    var chatSessions by remember { mutableStateOf(listOf<ChatViewModel.ChatSession>()) }
    var userID by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.dataStoreManager.getUserIdFromToken()?.let { userId ->
            userID = userId
            viewModel.getChatSessionsForUser(userId.toString()) { sessions ->
                chatSessions = sessions
            }
        }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Sidebar(drawerState, navHostController, viewModel.dataStoreManager) {
        Column(modifier = Modifier.fillMaxSize()) {
            SmallElipseAndTitle("All chats", drawerState)
            if (chatSessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No chats available", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(chatSessions) { session ->
                        ChatSessionCard(chatSession = session) {
                            val user2Id = session.participants.firstOrNull { it != userID.toString() }
                            user2Id?.let {
                                val image = "default.png" // Replace with actual logic to get the image
                                navHostController.navigate("${Screen.Chat.route}/${session.chatId}/$it/$image")
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ChatSessionCard(chatSession: ChatViewModel.ChatSession, onChatSelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onChatSelected(chatSession.chatId) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Text(
            text = "Chat: ${chatSession.chatId}",
            modifier = Modifier.padding(16.dp)
        )
    }
}