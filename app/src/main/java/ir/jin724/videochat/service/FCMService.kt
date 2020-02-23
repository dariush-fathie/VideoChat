package ir.jin724.videochat.service

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.ui.main.MainActivity
import ir.jin724.videochat.util.decodeBase64
import ir.jin724.videochat.util.notifications.NotifiManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber


class FCMService : FirebaseMessagingService() {

    private val tag = this::class.java.simpleName

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val message = p0.data["message"].toString().decodeBase64()

        NotifiManager.sendNotification(
            this,
            MainActivity::class.java,
            1254,
            NotifiManager.MESSAGE,
            "this is title",
            message
        )
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        FirebaseMessaging.getInstance().subscribeToTopic("public").addOnSuccessListener {
            Timber.tag(tag).e("SUBSCRIBED TO TOPIC(PUBLIC)")
        }
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