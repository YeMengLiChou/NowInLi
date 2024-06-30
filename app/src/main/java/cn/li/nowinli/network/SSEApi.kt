package cn.li.nowinli.network

import retrofit2.http.Body
import retrofit2.http.POST

interface SSEApi {

    @POST("/send")
    suspend fun sendEvent(@Body msg: String): ApiResponse<String>

}

