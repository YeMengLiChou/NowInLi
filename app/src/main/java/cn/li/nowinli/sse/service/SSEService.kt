package cn.li.nowinli.sse.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import cn.li.nowinli.BuildConfig
import cn.li.nowinli.R
import cn.li.nowinli.network.OkHttpClientInstance
import cn.li.nowinli.network.OkHttpClientType
import cn.li.nowinli.network.SSEApi
import cn.li.nowinli.utils.checkPermissionGranted
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.sse.RealEventSource
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@AndroidEntryPoint
class SSEService : Service() {
    private val sseRequest =
        Request.Builder().url("${BuildConfig.BASE_URL}/notification").build()

    @OkHttpClientInstance(OkHttpClientType.SSE)
    @Inject
    lateinit var okHttpClient: OkHttpClient

    private lateinit var realEventSource: RealEventSource

    private lateinit var handler: Handler

    private val scope = CoroutineScope(EmptyCoroutineContext)

    private var notificationId = 1

    private val notificationChannelId = "SSE-Notification"

    @Inject
    lateinit var mapper: ObjectMapper
    companion object {
        private val TAG = SSEService::class.simpleName
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())

        Log.d(TAG, "onCreate: ")
        scope.launch(Dispatchers.IO) {
            realEventSource = RealEventSource(sseRequest, object : EventSourceListener() {
                override fun onClosed(eventSource: EventSource) {
                    Log.d(TAG, "onClosed: ")
                    handler.post {
                        Toast.makeText(this@SSEService, "closed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    Log.d(TAG, "    onEvent: ")
                    handler.post {
                        if (type == "connect") {
                            Toast.makeText(this@SSEService, data, Toast.LENGTH_SHORT).show()
                        } else {
                            sendNotification(data)
                        }
                    }
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: Response?
                ) {
                    // retry to connect
                    handler.post {
                        Toast.makeText(this@SSEService, t.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onOpen(eventSource: EventSource, response: Response) {
                    Log.d(TAG, "onOpen: ")
                    handler.post {
                        Toast.makeText(this@SSEService, "open", Toast.LENGTH_SHORT).show()
                    }
                }
            }).also {
                // 发送 sse 连接
                it.connect(okHttpClient)
                Log.d(TAG, "onCreate: $it")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: $intent $flags $startId")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        if (::realEventSource.isInitialized) {
            realEventSource.cancel()
        }
        Log.d(TAG, "onDestroy: ")
    }


    @SuppressLint(value = ["MissingPermission", "InlinedApi"])
    private fun sendNotification(value: String) {
        val message = mapper.readValue(value, NotificationBean::class.java).message
        checkPermissionGranted(this, Manifest.permission.POST_NOTIFICATIONS)
            .onGranted {
                cn.li.nowinli.utils.sendNotification(
                    context = this,
                    notificationId = notificationId++,
                    channelFactory = {
                        NotificationChannelCompat.Builder(
                            notificationChannelId,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                            .setDescription("发送SSE服务器发送的数据通知")
                            .setName("SSE服务器消息")
                            .setLightsEnabled(true)
                            .setLightColor(Color.RED)
                            .setVibrationEnabled(true)
                            .build()
                    }
                ) {
                    NotificationCompat.Builder(this, it!!)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle("SSE")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setLargeIcon(
                            AppCompatResources.getDrawable(this, R.drawable.ic_launcher_foreground)
                                ?.toBitmap()
                        )
                        .setWhen(System.currentTimeMillis() + 1000)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .build()
                }
            }.onDenied {
                Toast.makeText(this, "请授予通知权限", Toast.LENGTH_SHORT).show()
            }

    }
}