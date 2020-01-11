package ir.jin724.videochat.ui.chat

import android.app.Application
import androidx.lifecycle.*
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.data.chatRepository.ChatRepository
import ir.jin724.videochat.util.Constants
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatRepository = ChatRepository()

    init {
        setUpOnNewMessage()
    }


    fun getChatHistory(myId: Int, bobId: Int) = liveData {
        val result = chatRepository.getChatHistory(myId, bobId)
        emit(result)
    }


    private val chatItemTrigger = MutableLiveData<ChatItem>()

    fun sendMessage(chatItem: ChatItem) {
        chatItemTrigger.value = chatItem
    }

    val sendChatResult = chatItemTrigger.switchMap { chatItem ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(chatRepository.sendMessage(chatItem))
        }
    }


    private val newMessageTrigger = MutableLiveData<ChatItem>()

    private fun setUpOnNewMessage() {
        getApplication<VideoChatApp>().socket.on(Constants.NEW_MESSAGE) {
            val chatItem = VideoChatApp.gson.fromJson<ChatItem>(
                (it[0] as JSONObject).toString(),
                ChatItem::class.java
            )
            newMessageTrigger.postValue(chatItem)
        }

        getApplication<VideoChatApp>().socket
    }

    val liveNewMessage = newMessageTrigger.map {
        it
    }


}