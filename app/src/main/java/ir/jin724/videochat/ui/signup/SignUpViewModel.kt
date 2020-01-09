package ir.jin724.videochat.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import ir.jin724.videochat.data.userRepository.UserRepository
import kotlinx.coroutines.Dispatchers

class SignUpViewModel : ViewModel() {

    private val repository = UserRepository()

    fun signUp(firstName: String, lastName: String) =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val user = repository.signUp(firstName, lastName)
            emit(user)
        }


}