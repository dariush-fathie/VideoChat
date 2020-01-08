package ir.jin724.videochat.data.userRepository

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {

    @GET("/getAllUsers")
    suspend fun getAllUsers(): List<User>

    @GET("/signUp")
    suspend fun signUp(
        @Query("firstName") firstName: String,
        @Query("lastName") lastName: String
    ) : User

}