package ir.jin724.videochat

import android.app.Application
import timber.log.Timber


class VideoChatApp : Application() {


    companion object {
        var token = "dm4sBIPWElk:APA91bFaYm-Duq3z7dcTalnounO45-Oi0JVV6mW-ljkCl0gKFLtW_c2gaPjatRvoF79LtEJTLrtZzbE3J_eNAHOGvPGfne8epKeHaP0FJ0eZ6q5zES4n7gGUqlwFl4eDBa0aBLXsKtL7"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }


}