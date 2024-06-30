package cn.li.nowinli.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


@Retention(AnnotationRetention.SOURCE)
@IntDef(
    NotificationManager.IMPORTANCE_DEFAULT,
    NotificationManager.IMPORTANCE_HIGH,
    NotificationManager.IMPORTANCE_LOW,
    NotificationManager.IMPORTANCE_UNSPECIFIED
)
annotation class ChannelImportance



internal fun sendNotificationImpl(
    manager: NotificationManagerCompat

) {
//    manager.getNotificationChannel()
}

fun sendNotification(
    context: Context,
    channelId: String,
    channelName: String,
    notificationId: Int,
    notification: Notification
) {
    val manager = NotificationManagerCompat.from(context)

    val channel = manager.getNotificationChannel(channelId, channelName)
//    NotificationCompat.Builder


//    if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
//        sendNotificationApi26Impl(
//            manager,
//            channelId,
//            channelName,
//            NotificationManager.IMPORTANCE_DEFAULT,
//            notificationId,
//            notification
//        )
//    } else {
//        sendNotificationImpl()
//    }
}