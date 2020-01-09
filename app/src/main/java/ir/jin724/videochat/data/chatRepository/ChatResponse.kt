package ir.jin724.videochat.data.chatRepository

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("chat_item_id")
    @Expose
    val chatItemId: Int = -1,

    @SerializedName("delivered")
    @Expose
    val delivered:Boolean
)