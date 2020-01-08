package ir.jin724.videochat.data.userRepository

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(

    @Expose
    @SerializedName("userId")
    val userId: Int,

    @Expose
    @SerializedName("clientId")
    val clientId: String,

    @Expose
    @SerializedName("firstName")
    val firstName: String,

    @Expose
    @SerializedName("lastName")
    val lastName: String
)