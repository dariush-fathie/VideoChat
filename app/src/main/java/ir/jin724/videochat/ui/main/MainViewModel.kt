package ir.jin724.videochat.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.data.userRepository.UserRepository
import ir.jin724.videochat.util.Constants
import org.json.JSONObject
import timber.log.Timber


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository()
    private val socket = (application as VideoChatApp).socket
    private val gson = Gson()

    fun getAllUsers() = liveData {
        val users = repository.getAllUsers()
        emit(users)
    }

    private val connectionTrigger = MutableLiveData<User>()
    private val disconnectTrigger = MutableLiveData<String>()

    val connectionEvent: LiveData<User> = Transformations.map(connectionTrigger) {
        it
    }

    val disconnectEvent: LiveData<String> = Transformations.map(disconnectTrigger) {
        it
    }

    private var flag = false

    fun observeUsers(user: User) {
        if (!flag) {
            flag = true

            socket.on(Constants.EVENT_NEW_CONNECTION) {
                if (it.isNotEmpty()) {
                    val newUser = gson.fromJson<User>((it[0] as JSONObject).toString(), User::class.java)
                    connectionTrigger.postValue(newUser)
                }
            }

            socket.on(Constants.EVENT_DISCONNECTED) {
                if (it.isNotEmpty()) {
                    val clientId = it[0] as String
                    disconnectTrigger.postValue(clientId)
                }
            }

            socket.on(Constants.YOU_ARE_ONLINE) {
                Timber.e(Constants.YOU_ARE_ONLINE)
            }

            socket.connect()
            socket.emit(Constants.I_AM_ONLINE, user.userId)
        }

    }

}