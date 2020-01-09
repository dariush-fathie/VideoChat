package ir.jin724.videochat.data.userRepository


import ir.jin724.videochat.BuildConfig
import ir.jin724.videochat.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class UserRepository {

    private val userService: UserService

    init {

        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            if (BuildConfig.DEBUG) {
                Timber.tag("OkHttp").e(message)
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
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