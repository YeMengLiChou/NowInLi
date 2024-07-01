# 结合Service实现SSE通知推送

在 Service 中启用 SSE 连接，保持和服务器的长连接，并接受服务器发送过来的消息

然后将接受到的消息通过 `Notification` 展示给用户

```kotlin
```


# Ktor 后端代码

```kotlin

data class NotificationBean(
    val message: String,
    val timestamp: Long,

    )

private const val EVENT_CONNECT = "connect"
private const val EVENT_NOTIFICATION = "notification"

private val notifications = MutableSharedFlow<NotificationBean>(replay = 5)


fun Application.notification() {
    routing {
        sse("/notification") {
            val session = this
            send(data = "connected", event = EVENT_CONNECT)
            notifications.collect { notification ->
                try {
                    send(ObjectMapper().writeValueAsString(notification), event = EVENT_NOTIFICATION)
                } catch (e: Exception) {
                    log.error(e)
                    session.close()
                    session.cancel()
                }
            }
        }

        post("/notification/send") {
            val msg = call.receive<String>()
            notifications.emit(NotificationBean(msg, System.currentTimeMillis()))
            call.respond(ApiResult(200, "ok", null))
        }
    }
}
```