package cn.li.nowinli.network

import retrofit2.http.Body
import retrofit2.http.POST

interface SSEApi {

    /**
     * 向服务器发送 [msg]，服务器向sse发送该 [msg]
     * */
    @POST("/send")
    suspend fun sendEvent(@Body msg: String): ApiResponse<String>

    /**
     * 发送通知，触发服务器向sse发送通知
     * */
    @POST("/notification/send")
    suspend fun sendNotification(@Body msg: String): ApiResponse<String>
}

