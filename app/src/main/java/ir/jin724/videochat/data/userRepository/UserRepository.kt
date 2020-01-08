package ir.jin724.videochat.data.userRepository


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepository {

    private val userService: UserService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)
    }


    suspend fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    suspend fun signUp(firstName: String, lastName: String): User {
        return userService.signUp(firstName, lastName)
    }

}