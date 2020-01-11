package ir.jin724.videochat

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import ir.jin724.videochat.util.Constants
import ir.jin724.videochat.util.PrefsManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


class VideoChatApp : Application() {

    companion object {
        private const val SIGNALING_URI = Constants.BASE_URL

        private val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            if (BuildConfig.DEBUG) {
                Timber.tag("OkHttp").e(message)
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY)

        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val gson = Gson()
    }

    val socket: Socket by lazy { IO.socket(SIGNALING_URI) }
    lateinit var prefsManager :PrefsManager

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        prefsManager = PrefsManager(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

}