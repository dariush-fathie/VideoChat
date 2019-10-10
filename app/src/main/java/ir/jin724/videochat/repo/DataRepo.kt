package ir.jin724.videochat.repo

import android.content.Context
import android.widget.Toast
import ir.jin724.videochat.VideoChatApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import timber.log.Timber

class DataRepo(private val context: Context) {

    companion object {

        val client = Retrofit.Builder()
            .baseUrl("")
            .build()


    }


    private fun createDataService(): DataService {
        return client.create(DataService::class.java)
    }

    fun sendData(data: Any?) {
        createDataService().sendData(VideoChatApp.token.also {
            if (it == "")
                throw Exception("empty token")

        }, data.toString()).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("error repo , ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText(context, "ارسال شد", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface DataService {

        @POST("sendData")
        @FormUrlEncoded
        fun sendData(@Field("token") token: String, @Field("data") data: String): Call<String>

    }

}