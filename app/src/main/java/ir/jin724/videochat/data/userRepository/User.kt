package ir.jin724.videochat.data.userRepository

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.jin724.videochat.VideoChatApp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(

    @Expose
    @SerializedName("user_id")
    val userId: Int,

    @Expose
    @SerializedName("client_id")
    val clientId: String = "",

    @Expose
    @SerializedName("first_name")
    val firstName: String,

    @Expose
    @SerializedName("last_name")
    val lastName: String,

    @Expose
    @SerializedName("avatar_url")
    var avatar: String,

    @Expose
    @SerializedName("state")
    var state: String = "offline"
) : Parcelable {

    fun isOnline() = state == "online"

    fun toJson(): String? {
        return VideoChatApp.gson.toJson(this, this::class.java)
    }

}