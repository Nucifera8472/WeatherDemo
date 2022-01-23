package at.nuceria.weatherdemo.data

import androidx.lifecycle.liveData
import at.nuceria.weatherdemo.BuildConfig
import at.nuceria.weatherdemo.data.remote.WeatherService
import at.nuceria.weatherdemo.data.remote.response.WeatherResponse
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService
) {
    private val apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY

    // only request the data that we plan on using in the app
    private val exclude = "alerts,minutely,hourly"

    fun getWeather(lat: Double, lon: Double) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            val response = fetchWeatherData(lat, lon)

            emit(Result.Success(response))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    private suspend fun fetchWeatherData(lat: Double, lon: Double): WeatherResponse {
        // TODO: don't use api models in app
        return weatherService.getWeatherData(lat, lon, exclude, apiKey)
    }

}
