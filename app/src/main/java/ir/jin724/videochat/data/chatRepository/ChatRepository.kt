package ir.jin724.videochat.data.chatRepository

import com.google.gson.Gson
import ir.jin724.videochat.VideoChatApp.Companion.retrofit

class ChatRepository {

    private val chatService = retrofit.create(ChatService::class.java)

    suspend fun sendMessage(chatItem: ChatItem): ChatResponse {
        val jsonChatItem = Gson().toJson(chatItem)
        return chatService.sendMessage(jsonChatItem)
    }

    suspend fun getChatHistory(myId: Int, bobId: Int): List<ChatItem> {
        return chatService.getChatHistory(myId, bobId)
    }


}