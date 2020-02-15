package ir.jin724.videochat.service

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ir.jin724.videochat.VideoChatApp
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class FCMService : FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }


    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }


    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        startService(Intent(this, TestService::class.java))
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe
    fun onApplicationStatusChanged(status: VideoChatApp.ApplicationLifecycleObserver.ApplicationStatus) {
        // todo status changed
    }


}