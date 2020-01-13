package ir.jin724.videochat.ui.chat

import android.app.Application
import androidx.lifecycle.*
import io.socket.client.Socket
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.VideoChatApp.Companion.gson
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.data.chatRepository.ChatRepository
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.util.Constants
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import timber.log.Timber

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatRepository = ChatRepository()
    private val socket: Socket = getApplication<VideoChatApp>().socket
    private val chatItemTrigger = MutableLiveData<ChatItem>()
    private val newMessageTrigger = MutableLiveData<ChatItem>()
    private val isWritingTrigger = MutableLiveData<Any>()

    init {
        setUpSocket()
    }


    fun getChatHistory(myId: Int, bobId: Int) = liveData {
        val result = chatRepository.getChatHistory(myId, bobId)
        emit(result)
    }


    fun sendMessage(chatItem: ChatItem) {
        chatItemTrigger.value = chatItem
    }

    val sendChatResult = chatItemTrigger.switchMap { chatItem ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(chatRepository.sendMessage(chatItem))
        }
    }


    private fun setUpSocket() {
        socket.on(Constants.NEW_MESSAGE) {
            val chatItem = VideoChatApp.gson.fromJson<ChatItem>(
                (it[0] as JSONObject).toString(),
                ChatItem::class.java
            )
            newMessageTrigger.postValue(chatItem)
        }

        socket.on(Constants.IS_WRITING) {
            // todo get writer for public chat group
            isWritingTrigger.postValue(true)
            Timber.e("isWriting ...")
        }
    }

    val liveNewMessage = newMessageTrigger.map {
        it
    }


    fun emitIsWriting(me: User, bob: User) {
        socket.emit(Constants.IS_WRITING , me.toJson(gson) , bob.toJson(gson))
    }

    val isWritingEvent = isWritingTrigger.map {
        it
    }

}