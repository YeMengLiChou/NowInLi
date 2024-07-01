package cn.li.nowinli.utils

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat


/**
 * 发送通知
 * @param context
 * @param notificationId 通知唯一的id
 * @param channelFactory 通知 Channel，Android 8.0 以上需要创建 channel，可以使用 [NotificationChannelCompat] 创建
 * @param notificationFactory 创建通知内容
 * */
@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun sendNotification(
    context: Context,
    notificationId: Int,
    channelFactory: () -> NotificationChannelCompat?,
    notificationFactory: (channelId: String?) -> Notification
) {
    val manager = NotificationManagerCompat.from(context)
    val channel = channelFactory.invoke()
    // api >= 26 需要 channel
    checkVersion(Build.VERSION_CODES.O) {
        checkNotNull(channel) { "channel must not be null" }
        manager.createNotificationChannel(channel)
    }
    val notification = notificationFactory.invoke(channel?.id)
    manager.notify(notificationId, notification)
}

