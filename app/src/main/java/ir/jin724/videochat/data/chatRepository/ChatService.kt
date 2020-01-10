package ir.jin724.videochat.data.chatRepository

import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatService {

    @GET("getChatHistory")
    suspend fun getChatHistory(
        @Query("my_id") myId: Int,
        @Query("bob_id") bobId: Int
    ): List<ChatItem>


    @POST("sendMessage")
    suspend fun sendMessage(
        @Field("chat_item") chatItem: String
    ): ChatResponse


}