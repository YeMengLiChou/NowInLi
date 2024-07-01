package cn.li.nowinli.sse.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.nowinli.BuildConfig
import cn.li.nowinli.network.OkHttpClientInstance
import cn.li.nowinli.network.OkHttpClientType
import cn.li.nowinli.network.SSEApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.sse.RealEventSource
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import javax.inject.Inject

@HiltViewModel
class SSEViewModel @Inject constructor(
    @OkHttpClientInstance(OkHttpClientType.SSE) okHttpClient: OkHttpClient,
    private val sseApi: SSEApi
) :
    ViewModel() {

    private val sseRequest =
        Request.Builder().url("${BuildConfig.BASE_URL}/events").method("GET", null).build()
            .also {
                Log.d("SSEViewModel", "url: ${BuildConfig.BASE_URL} ")
            }


    private val _realEventSourceState = MutableSharedFlow<SSEState>(2).also {
        it.tryEmit(SSEState.Nothing)
    }

    val realEventSourceState = _realEventSourceState.asSharedFlow()

    private lateinit var realEventSource: RealEventSource

    init {
        viewModelScope.launch(Dispatchers.IO) {
            realEventSource = RealEventSource(sseRequest, object : EventSourceListener() {

                override fun onClosed(eventSource: EventSource) {
                    _realEventSourceState.tryEmit(SSEState.Closed)
                }

                override fun onEvent(
                    eventSource: EventSource, id: String?, type: String?, data: String
                ) {
                    _realEventSourceState.tryEmit(SSEState.Event(id, type, data))
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: Response?
                ) {
                    _realEventSourceState.tryEmit(SSEState.Failure(t, response))
                }

                override fun onOpen(eventSource: EventSource, response: Response) {
                    _realEventSourceState.tryEmit(SSEState.Open(response))
                }
            }).also {
                // 发送 sse 连接
                viewModelScope.launch(Dispatchers.IO) {
                    it.connect(okHttpClient)
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        if (::realEventSource.isInitialized) {
            realEventSource.cancel()
        }
    }


    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sseApi.sendEvent(message)
        }
    }

    fun sendNotification(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sseApi.sendNotification(message)
        }
    }
}

sealed interface SSEState {

    data object Nothing : SSEState

    data object Closed : SSEState

    data class Event(val id: String?, val type: String?, val data: String) : SSEState

    data class Failure(val t: Throwable?, val response: Response?) : SSEState

    data class Open(val response: Response) : SSEState
}