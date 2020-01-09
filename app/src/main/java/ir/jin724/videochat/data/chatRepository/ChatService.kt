package ir.jin724.videochat.data.chatRepository

import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatService {


    @GET("getChatHistory")
    suspend fun getChatHistory(
        @Field("userId") userId: Int,
        @Field("otherUserId") otherUserId: Int
    ): List<ChatItem>


    @POST("sendMessage")
    suspend fun sendMessage(): ChatResponse


}