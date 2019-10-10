package ir.jin724.videochat

import android.app.Application
import timber.log.Timber


class VideoChatApp : Application() {


    companion object {
        var token = ""
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }


}