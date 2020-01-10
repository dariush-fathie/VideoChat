package ir.jin724.videochat.data.userRepository


import ir.jin724.videochat.VideoChatApp.Companion.retrofit

class UserRepository {

    private val userService: UserService = retrofit.create(UserService::class.java)


    suspend fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    suspend fun signUp(firstName: String, lastName: String): User {
        return userService.signUp(firstName, lastName)
    }

}