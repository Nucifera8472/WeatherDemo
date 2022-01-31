package at.nuceria.weatherdemo.di

import at.nuceria.weatherdemo.data.remote.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * This module is responsible for creating the necessary instances for api communication.
 */

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val OPEN_WEATHER_MAP_BASE_URL = "https://api.openweathermap.org"

    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        jsonConverterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_MAP_BASE_URL)
            .addConverterFactory(jsonConverterFactory) // to customize the (de)serialization strategy to use kotlinx serialization
            .client(okHttpClient)
            .build()
    }
}
