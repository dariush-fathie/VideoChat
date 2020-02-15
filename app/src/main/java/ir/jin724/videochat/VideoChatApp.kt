package ir.jin724.videochat

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import androidx.multidex.MultiDex
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import ir.jin724.videochat.util.Constants
import ir.jin724.videochat.util.DateConverter
import ir.jin724.videochat.util.PrefsManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class VideoChatApp : Application() {

    companion object {
        private const val SIGNALING_URI = Constants.BASE_URL

        private val loggingInterceptor =
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
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

        val dateConverter = DateConverter.getInstance()

        var applicationStatus = MutableLiveData<ApplicationLifecycleObserver.ApplicationStatus>()
            private set(value) {
                EventBus.getDefault().post(value)
                // todo check this later
                field = value
            }

    }

    val socket: Socket by lazy { IO.socket(SIGNALING_URI) }
    lateinit var prefsManager: PrefsManager

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        prefsManager = PrefsManager(this)

        Toast.makeText(this, "app created", Toast.LENGTH_SHORT).show()
        //ProviderUtil.allAlgorithm()

        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }


    open class ApplicationLifecycleObserver : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            applicationStatus.value = ApplicationStatus.BACKGROUND
        }


        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            applicationStatus.value = ApplicationStatus.FOREGROUND
        }


        enum class ApplicationStatus {
            BACKGROUND, FOREGROUND
        }

    }


}