package ir.jin724.videochat

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import androidx.multidex.MultiDex
import com.google.gson.Gson
import io.socket.client.Socket
import ir.jin724.videochat.util.Constants
import ir.jin724.videochat.util.DateConverter
import ir.jin724.videochat.util.PrefsManager
import ir.jin724.videochat.webRTC.CustomTrust
import ir.jin724.videochat.webRTC.SocketFactory
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class VideoChatApp : Application() {

    val tag = VideoChatApp::class.java.simpleName

    companion object {
        private lateinit var ctx: Context

        val retrofit: Retrofit by lazy {
            val customTrust = CustomTrust(ctx)
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(customTrust.client)
                .build()
            retrofit
        }


        val gson = Gson()

        val dateConverter = DateConverter.getInstance()

        var applicationStatus = MutableLiveData<ApplicationLifecycleObserver.ApplicationStatus>()
            private set(value) {
                EventBus.getDefault().post(value)
                // todo check this later
                field = value
            }
    }

    val socket: Socket by lazy {
        SocketFactory.getSecureSocket(CustomTrust(this)) ?: throw Exception("null socket")
    }

    lateinit var prefsManager: PrefsManager

    override fun onCreate() {
        super.onCreate()
        ctx = this

        Timber.plant(Timber.DebugTree())
        prefsManager = PrefsManager(this)

        Toast.makeText(this, "app created", Toast.LENGTH_SHORT).show()
        //ProviderUtil.allAlgorithm()

        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver(this))
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }


    open class ApplicationLifecycleObserver(private val context: Context) : LifecycleObserver {
        private val tag = "APPLICATION_LIFECYCLE"

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            applicationStatus.value = ApplicationStatus.BACKGROUND
            Timber.tag(tag).e("ON_STOP")
            Toast.makeText(context, "APPLICATION STOP", Toast.LENGTH_SHORT).show()
        }


        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            applicationStatus.value = ApplicationStatus.FOREGROUND
            Timber.tag(tag).e("ON_START")
            Toast.makeText(context, "APPLICATION START", Toast.LENGTH_SHORT).show()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            applicationStatus.value = ApplicationStatus.FOREGROUND
            Timber.tag(tag).e("ON_DESTROY")
            Toast.makeText(context, "APPLICATION DESTROY", Toast.LENGTH_SHORT).show()
        }

        enum class ApplicationStatus {
            BACKGROUND, FOREGROUND
        }

    }


}