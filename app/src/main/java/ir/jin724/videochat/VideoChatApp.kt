package ir.jin724.videochat

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import io.socket.client.IO
import io.socket.client.Socket
import ir.jin724.videochat.util.Constants
import ir.jin724.videochat.util.PrefsManager
import timber.log.Timber


class VideoChatApp : Application() {

    companion object {
        private const val SIGNALING_URI = Constants.BASE_URL
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