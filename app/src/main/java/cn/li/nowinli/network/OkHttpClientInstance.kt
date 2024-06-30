package cn.li.nowinli.network

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class OkHttpClientInstance(
    val type: OkHttpClientType
)



enum class OkHttpClientType {
    NORMAL,
    SSE
}

