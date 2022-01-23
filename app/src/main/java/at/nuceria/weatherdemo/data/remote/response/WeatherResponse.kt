package at.nuceria.weatherdemo.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class WeatherResponse(
    val lat: Double,
    val lon: Double,
    @SerialName("current")
    val currentWeather: CurrentWeather,
    @SerialName("daily")
    val dailyWeather: List<DailyWeather>
)

/**
 * @param requestTimestamp Unix UTC
 * @param windDegrees degrees meteorological
 */
@Serializable
class CurrentWeather(
    @SerialName("dt")
    val requestTimestamp: Long,
    val temp: Float,
    @SerialName("feels_like")
    val feelsLikeTemp: Float,
    @SerialName("wind_speed")
    val windSpeed: Float,
    @SerialName("wind_deg")
    val windDegrees: Int,
    @SerialName("weather")
    val weathers: List<Weather>
)

@Serializable
class DailyWeather(
    @SerialName("dt")
    val dateTime: Long,
    @SerialName("temp")
    val dayTemperatures: DayTemperatures,
    @SerialName("feels_like")
    val feelsLikeTemperatures: DayFeelsLikeTemperatures,
    @SerialName("wind_speed")
    val windSpeed: Float,
    @SerialName("wind_deg")
    val windDegrees: Int,
    @SerialName("weather")
    val weatherConditions: List<Weather>
)

@Serializable
class DayTemperatures(
    val morn: Float,
    val day: Float,
    val eve: Float,
    val night: Float,
    val min: Float,
    val max: Float,
)

@Serializable
class DayFeelsLikeTemperatures(
    val morn: Float,
    val day: Float,
    val eve: Float,
    val night: Float
)

@Serializable
class Weather(
    val id: Int,
    val description: String
)
