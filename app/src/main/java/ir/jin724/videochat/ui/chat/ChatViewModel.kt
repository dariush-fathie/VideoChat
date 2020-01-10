package ir.jin724.videochat.ui.chat

import androidx.lifecycle.*
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.data.chatRepository.ChatRepository
import kotlinx.coroutines.Dispatchers

class ChatViewModel : ViewModel() {

    private val chatRepository = ChatRepository()

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

}