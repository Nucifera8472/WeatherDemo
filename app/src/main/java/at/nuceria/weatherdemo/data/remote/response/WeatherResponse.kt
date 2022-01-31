package at.nuceria.weatherdemo.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    @SerialName("timezone")
    val timezoneId: String,
    @SerialName("current")
    val currentWeather: CurrentWeather,
    @SerialName("daily")
    val dailyWeather: List<DailyWeather>
)

/**
 * @param requestTimestamp Unix UTC
 * @param windDegrees degrees meteorological
 * @param rain only where available
 * @param snow only where available
 */
@Serializable
data class CurrentWeather(
    @SerialName("dt")
    val requestTimestamp: Long,
    val temp: Float,
    @SerialName("feels_like")
    val feelsLikeTemp: Float,
    val pressure: Int,
    val humidity: Int,
    @SerialName("wind_speed")
    val windSpeed: Float,
    @SerialName("wind_deg")
    val windDegrees: Int,
    @SerialName("weather")
    val weatherConditions: List<Weather>,
    val rain: Rain? = null,
    val snow: Snow? = null,
)

@Serializable
data class Rain(
    @SerialName("1h")
    val lastHourAmount: Float
)

@Serializable
data class Snow(
    @SerialName("1h")
    val lastHourAmount: Float
)

/**
 * @param moonPhase Moon phase. 0 and 1 are 'new moon', 0.25 is 'first quarter moon', 0.5 is 'full moon' and 0.75 is 'last quarter moon'. The periods in between are called 'waxing crescent', 'waxing gibous', 'waning gibous', and 'waning crescent', respectively.
 */
@Serializable
data class DailyWeather(
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
    val weatherConditions: List<Weather>,
    @SerialName("pop")
    val precipitationProbability: Float,
    val pressure: Int,
    val humidity: Int,
    @SerialName("dew_point")
    val dewPoint: Float,
    val sunrise: Long,
    val sunset: Long,
    val moonset: Long,
    val moonrise: Long,
    @SerialName("moon_phase")
    val moonPhase: Float,
    val rain: Float? = null,
    val snow: Float? = null,
)

@Serializable
data class DayTemperatures(
    val morn: Float,
    val day: Float,
    val eve: Float,
    val night: Float,
    val min: Float,
    val max: Float,
)

@Serializable
data class DayFeelsLikeTemperatures(
    val morn: Float,
    val day: Float,
    val eve: Float,
    val night: Float
)

@Serializable
data class Weather(
    val id: Int,
    val description: String
)
