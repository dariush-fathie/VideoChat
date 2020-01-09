package ir.jin724.videochat.data.chatRepository


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatItem(

    @Expose
    @SerializedName("chat_item_id")
    val ChatItemId: Int = -1,

    @Expose
    @SerializedName("text")
    val text: String,

    @Expose
    @SerializedName("from")
    val from: Int,

    @Expose
    @SerializedName("to")
    val to: Int,

    @Expose
    @SerializedName("delivered")
    val delivered: Boolean = false,

    @Expose
    @SerializedName("time")
    var time: String = ""
)