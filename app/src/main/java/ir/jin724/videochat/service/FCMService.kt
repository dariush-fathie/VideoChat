package ir.jin724.videochat.service

import android.app.Notification
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ir.jin724.videochat.R
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.ui.call.CallActivity
import ir.jin724.videochat.ui.main.MainActivity
import ir.jin724.videochat.util.decodeBase64
import ir.zadak.zadaknotify.notification.ZadakNotification
import ir.zadak.zadaknotify.pendingintent.ClickPendingIntentActivity
import ir.zadak.zadaknotify.pendingintent.DismissPendingIntentBroadCast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber


class FCMService : FirebaseMessagingService() {

    private val tag = this::class.java.simpleName

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val message = p0.data["message"].toString().decodeBase64()

        ZadakNotification.with(this)
            .load()
            .notificationChannelId("channelId-test")
            .identifier(21)
            .title("this is title")
            .message(message)
            .inboxStyle(
                arrayOf("first message", "second message", "third message"),
                "this is inbox title",
                "this is inbox summary"
            )
            .smallIcon(R.drawable.ic_check_24dp)
            .largeIcon(R.mipmap.ic_launcher)
            .flags(Notification.DEFAULT_ALL)
            .button(R.drawable.ic_video_24dp, "start call") {
                ClickPendingIntentActivity(
                    CallActivity::class.java,
                    null,
                    123
                ).onSettingPendingIntent()
            }
            .button(R.drawable.ic_microphone_24dp, "micro") {
                DismissPendingIntentBroadCast(null, 1231).onSettingPendingIntent()
            }
            .click(MainActivity::class.java)
            .dismiss(MainActivity::class.java)
            .color(R.color.colorPrimary)
            .ticker("tkicker")
            .`when`(5000)
            .vibrate(LongArray(3) {
                50
            })
            .lights(R.color.colorPrimaryDark, 500, 500)
            //.autoCancel(true)
            .simple()
            .build()

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