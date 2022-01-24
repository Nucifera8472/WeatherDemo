package at.nuceria.weatherdemo.data

import androidx.room.withTransaction
import at.nuceria.weatherdemo.BuildConfig
import at.nuceria.weatherdemo.data.local.AppDatabase
import at.nuceria.weatherdemo.data.local.CurrentWeatherDao
import at.nuceria.weatherdemo.data.local.DailyWeatherDao
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.data.remote.WeatherService
import at.nuceria.weatherdemo.data.remote.response.WeatherResponse
import at.nuceria.weatherdemo.util.toWeatherData
import at.nuceria.weatherdemo.util.networkBoundResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.joda.time.DateTime
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService,
    private val currentWeatherDao: CurrentWeatherDao,
    private val dailyWeatherDao: DailyWeatherDao,
    private val weatherDb: AppDatabase
) {
    private val apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY

    // only request the data that we plan on using in the app
    private val exclude = "alerts,minutely,hourly"

    fun getWeather(latitude: Double, longitude: Double) = networkBoundResource(
        query = {
            getWeatherDataFromDb()
        },
        fetch = {
            delay(2000)
            fetchWeatherData(latitude, longitude)
        },
        saveFetchResult = { weatherResponse ->
            val weatherData = weatherResponse.toWeatherData()
            weatherDb.withTransaction {
                currentWeatherDao.deleteAll()
                currentWeatherDao.insertAll(weatherData.currentWeatherData)
                dailyWeatherDao.deleteAll()
                dailyWeatherDao.insertAll(weatherData.forecastWeatherData + weatherData.todayOverView)
            }
        },
        shouldFetch = {
            // we always want to fetch if the current weather data is older than 1 hour
            it == null ||
                    DateTime.now().minusHours(1).isAfter(it.currentWeatherData.timeStamp * 1000)
        },
    )

    private fun getWeatherDataFromDb(): Flow<WeatherData?> {
        return currentWeatherDao.getCurrentWeatherFlow()
            .combine(dailyWeatherDao.getForecastDataFlow()) { current, daily ->
                if (current == null || daily.isEmpty()) {
                    null
                } else {
                    val todayOverview = daily.first()
                    val forecasts = daily.toMutableList().apply { removeFirst() }
                    WeatherData(current, todayOverview, forecasts)
                }
            }
    }

    private suspend fun fetchWeatherData(lat: Double, lon: Double): WeatherResponse {
        return weatherService.getWeatherData(lat, lon, exclude, apiKey)
    }


}



