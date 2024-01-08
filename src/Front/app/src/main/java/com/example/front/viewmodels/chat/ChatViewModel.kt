package com.example.front.viewmodels.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.front.helper.DataStore.DataStoreManager
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val database = Firebase.database("https://smalltrades-74681-default-rtdb.europe-west1.firebasedatabase.app").reference

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun setMessages(messages: List<Message>) {
        _messages.value = messages
    }


    fun addMessageToList(newMessage: Message) {
        val existingMessages = _messages.value
        if (newMessage !in existingMessages) {
            _messages.value = listOf(newMessage) + existingMessages
        } else {
            Log.d("ChatVM", "Message already in the list, skipping add.")
        }
    }


    fun checkOrCreateChatSession(user1Id: String, user2Id: String, onCompletion: (String) -> Unit) {
        Log.d("ChatVM", "checkOrCreateChatSession called with user1Id: $user1Id, user2Id: $user2Id")
        val chatId = generateChatId(user1Id, user2Id)

        database.child("chats").child(chatId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Chat session exists, return the existing chatId
                onCompletion(chatId)
            } else {
                val chatData = mapOf(
                    "participants" to mapOf(
                        user1Id to true,
                        user2Id to true
                    ),
                    "messages" to listOf<String>()
                )
                database.child("chats").child(chatId).setValue(chatData).addOnCompleteListener {
                    // After creating the chat, return the new chatId
                    onCompletion(chatId)
                }
            }
        }.addOnFailureListener {
        }
    }

    fun generateChatId(user1Id: String, user2Id: String): String {
        Log.d("ChatVM", "generateChatId called with user1Id: $user1Id, user2Id: $user2Id")
        val ids = listOf(user1Id, user2Id).sorted()
        return ids.joinToString("-")
    }
    fun sendMessage(chatId: String, senderId: String, messageText: String) {
        Log.d("ChatVM", "sendMessage called with chatId: $chatId, senderId: $senderId, messageText: $messageText")
        val messageId = database.child("chats").child(chatId).child("messages").push().key ?: return
        val messageData = mapOf(
            "sender" to senderId,
            "text" to messageText,
            "timestamp" to System.currentTimeMillis()
        )
        database.child("chats").child(chatId).child("messages").child(messageId).setValue(messageData)
            .addOnSuccessListener {
                Log.d("ChatVM", "Message successfully sent to Firebase")
            }
            .addOnFailureListener { e ->
                Log.e("ChatVM", "Error sending message to Firebase", e)
            }
        loadMessages(chatId) { messages ->
            setMessages(messages)
        }
    }

    private var hasInitialMessagesLoaded = false

    fun listenForMessages(chatId: String, onNewMessage: (Message) -> Unit) {
        Log.d("ChatVM", "listenForMessages called with chatId: $chatId")
        if (!hasInitialMessagesLoaded) {
            loadMessages(chatId) { messages ->
                messages.forEach { message ->
                    onNewMessage(message)
                }
                hasInitialMessagesLoaded = true
                continueListeningForNewMessages(chatId, onNewMessage)
            }
        } else {
            continueListeningForNewMessages(chatId, onNewMessage)
        }
    }

    private fun continueListeningForNewMessages(chatId: String, onNewMessage: (Message) -> Unit) {
        database.child("chats").child(chatId).child("messages").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue<Message>()
                message?.let { onNewMessage(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Implement if needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Implement if needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Implement if needed
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatVM", "Database error: ${error.message}")
            }
        })
    }

    fun loadMessages(chatId: String, onMessagesLoaded: (List<Message>) -> Unit) {
        Log.d("ChatVM", "loadMessages called with chatId: $chatId")
        database.child("chats").child(chatId).child("messages")
            .orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val messages = dataSnapshot.children.mapNotNull { it.getValue(Message::class.java) }
                        .sortedBy { it.timestamp } // Sort by timestamp to ensure order
                        .reversed() // Reverse the list to have the newest messages first
                    Log.d("ChatVM", messages.toString())
                    onMessagesLoaded(messages)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("loadMessages", "Database error: ${databaseError.message}")
                }
            })
    }
    fun getChatSessionsForUser(userId: String, onChatSessionsLoaded: (List<ChatSession>) -> Unit) {
        database.child("chats")
            .orderByChild("participants/$userId")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val chatSessions = dataSnapshot.children.mapNotNull { snapshot ->
                        val chatId = snapshot.key
                        // Assuming participants is a list of user IDs
                        val participantsSnapshot = snapshot.child("participants").children
                        val participants = participantsSnapshot.mapNotNull { it.key }
                        // If participants is indeed a map, use the below line instead:
                        // val participants = snapshot.child("participants").getValue<Map<String, Boolean>>() ?: emptyMap()
                        chatId?.let { ChatViewModel.ChatSession(it, participants) }
                    }
                    onChatSessionsLoaded(chatSessions)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("ChatVM", "Database error: ${databaseError.message}")
                }
            })
    }
    data class ChatSession(
        val chatId: String,
        val participants: List<String>
    )

}

data class Message(
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)