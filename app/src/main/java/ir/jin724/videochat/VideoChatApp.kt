package ir.jin724.videochat

import android.app.Application
import timber.log.Timber


class VideoChatApp : Application() {


    companion object {
        var token = "cm-Utme_NVg:APA91bHx3kK90HnowucVF351aZcO6gnIAbf00x6DJzPY5phnPblhejLVYgbgZNuoubPsKglH1Ven6oV4_nCaUr0VzAkcYQWfSilv3D8TEmcVnWMaNJrY4YjCw98XBHzrmmsCawdX_HQ1"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }


}