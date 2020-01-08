package ir.jin724.videochat

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber


class VideoChatApp : Application() {

    companion object {
        private const val SIGNALING_URI = "http://194.5.175.240:7000/"
    }

    val socket: Socket by lazy { IO.socket(SIGNALING_URI) }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

}