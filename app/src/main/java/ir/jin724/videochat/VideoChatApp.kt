package ir.jin724.videochat

import android.app.Application
import timber.log.Timber


class VideoChatApp : Application() {


    companion object {
        var token = "fG3UeYxQvYM:APA91bHEvOXNHqstKT2FO7OFM0fZVZqR5H387QzQf6KZzrM-P9r7Jv8hJKkjm6KSCYzvkF4eQamt8HM6kjxbABrXA1NUKBpeOc_b28cRXbXjRqd_hS3M_D5c5RLEPHOvBb8O1z-kRNM-"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }


}