package ir.jin724.videochat

import android.app.Application
import timber.log.Timber


class VideoChatApp : Application() {

    var token: String
        set(value) {
            getSharedPreferences("videoChat", 0).edit().putString("token", value).apply()
        }
        get() {
            return getSharedPreferences("videoChat", 0).getString("token", "")!!
        }


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())


        Timber.e("Token = $token")

    }


}