package at.nuceria.weatherdemo.data.remote

import at.nuceria.weatherdemo.data.remote.response.WeatherResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("${API_VERSION_PATH}/onecall?units=metric")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String
    ): WeatherResponse

    companion object {
        private const val API_VERSION_PATH = "/data/2.5"
    }

}


