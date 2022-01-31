package at.nuceria.weatherdemo.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import timber.log.Timber
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {

        val httpCacheDirectory: File = context.cacheDir
        val cacheSize = (1 * 1024 * 1024).toLong() // 1 MiB
        val cache = okhttp3.Cache(httpCacheDirectory, cacheSize)

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(defaultLoggingInterceptor())
            .build()
    }

    // Automatically retry a failed call once
//        @Provides
//        @Singleton
//        fun provideRetrofitRetryCallAdapterFactory(): RetryCallAdapterFactory {
//            return RetryCallAdapterFactory.create()
//        }

    @ExperimentalSerializationApi
    @Provides
    @Singleton
    fun provideJsonConverterFactory(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        return Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
//                serializersModule = customSerializer
        }.asConverterFactory(contentType)
    }

    private fun defaultLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("-- OkHttp").d(message)
            }
        })
        logging.level = HttpLoggingInterceptor.Level.BODY
        logging.redactHeader("Authorization")
        return logging
    }

}
