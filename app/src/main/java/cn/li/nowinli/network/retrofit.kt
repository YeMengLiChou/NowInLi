package cn.li.nowinli.network

import cn.li.nowinli.BuildConfig
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providesObjectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(
                KotlinModule.Builder()
                    .configure(KotlinFeature.StrictNullChecks, true)
                    .build()
            )
        }
    }

    @OkHttpClientInstance(type = OkHttpClientType.NORMAL)
    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
    }

    @OkHttpClientInstance(type = OkHttpClientType.SSE)
    @Provides
    fun providesSSEOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.HOURS)
            .writeTimeout(2, TimeUnit.HOURS)
            .build()
    }


    @Singleton
    @Provides
    fun providesRetrofit(
        mapper: ObjectMapper,
        @OkHttpClientInstance(type = OkHttpClientType.NORMAL)
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(
                JacksonConverterFactory.create(
                    mapper
                )
            )
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesSSEApi(retrofit: Retrofit): SSEApi {
        return retrofit.create(SSEApi::class.java)
    }
}

