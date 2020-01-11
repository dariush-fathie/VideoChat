package ir.jin724.videochat.data.chatRepository

import retrofit2.http.*

interface ChatService {

    @GET("getChatHistory")
    suspend fun getChatHistory(
        @Query("my_id") myId: Int,
        @Query("bob_id") bobId: Int
    ): List<ChatItem>


    @POST("sendMessage")
    @FormUrlEncoded
    suspend fun sendMessage(
        @Field("chat_item") chatItem: String
    ): ChatResponse


}