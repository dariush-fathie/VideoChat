package ir.jin724.videochat.util.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import ir.jin724.videochat.R

object NotifiManager {
    private const val GROUP_TED_PARK = "tedPark"

    var MESSAGE = "message"
    var COMMENT = "comment"
    var NOTICE = "notice"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val group1 =
                NotificationChannelGroup(GROUP_TED_PARK, GROUP_TED_PARK)
            getManager(context).createNotificationChannelGroup(group1)
            val channelMessage = NotificationChannel(
                MESSAGE,
                "메헤지", NotificationManager.IMPORTANCE_DEFAULT
            )
            channelMessage.description = "디스크립트"
            channelMessage.group = GROUP_TED_PARK
            channelMessage.lightColor = Color.GREEN
            channelMessage.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            getManager(context).createNotificationChannel(channelMessage)
            val channelComment = NotificationChannel(
                COMMENT,
                "코멘트", NotificationManager.IMPORTANCE_DEFAULT
            )
            channelComment.description = "코멘트드스크립트"
            channelComment.group = GROUP_TED_PARK
            channelComment.lightColor = Color.BLUE
            channelComment.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            getManager(context).createNotificationChannel(channelComment)
            val channelNotice = NotificationChannel(
                NOTICE,
                "알림", NotificationManager.IMPORTANCE_HIGH
            )
            channelNotice.description = "노티스디스크립트"
            channelNotice.group = GROUP_TED_PARK
            channelNotice.lightColor = Color.RED
            channelNotice.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            getManager(context).createNotificationChannel(channelNotice)
        }
    }

    private fun getManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun deleteChannel(context: Context, channel: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager(context).deleteNotificationChannel(channel)
        }
    }

    fun sendNotification(
        context: Context,
        activityClass: Class<out AppCompatActivity?>?,
        id: Int, channel: String?,
        title: String?,
        body: String?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder =
                Notification.Builder(context, channel)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
            getManager(context).notify(id, builder.build())
        } else {
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, activityClass),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val largeIcon = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.ic_check_double_24dp
            )
            val builder = NotificationCompat.Builder(context)
            builder.setSmallIcon(R.drawable.ic_check_24dp)
            builder.setContentTitle(title)
            builder.setContentText(body)
            builder.setDefaults(Notification.DEFAULT_SOUND)
            builder.setLargeIcon(largeIcon)
            builder.priority = NotificationCompat.PRIORITY_DEFAULT
            builder.setAutoCancel(true)
            builder.setContentIntent(pendingIntent)
            val notifiManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifiManager.notify(0, builder.build())
        }
    }

    private val smallIcon: Int
        private get() = android.R.drawable.stat_notify_chat


}