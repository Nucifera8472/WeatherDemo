package at.nuceria.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import at.nuceria.weatherdemo.util.epochToLocalTime

/**
 *
 * @param timeStamp Timestamp for this day at 0000 hours
 * @param localizedDescription Can be returned by the weather service, can be more detailed than
 * our supported condition types
 *
 *
 */
@Entity
data class DailyWeatherData(
    // As long as we only support 1 location, the day timestamp can be our key
    @PrimaryKey val timeStamp: Long,
    val timezoneId: String,
    val latitude: Double,
    val longitude: Double,
    val morningTemperature: Float,
    val dayTemperature: Float,
    val eveningTemperature: Float,
    val nightTemperature: Float,
    val minTemperature: Float,
    val maxTemperature: Float,
    val morningFeelsLikeTemperature: Float,
    val dayFeelsLikeTemperature: Float,
    val eveningFeelsLikeTemperature: Float,
    val nightFeelsLikeTemperature: Float,
    val condition: WeatherCondition,
    val localizedDescription: String,
    val windSpeed: Float,
    val windDegrees: Int,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: Float,
    val precipitationProbability: Float,
    val precipitationAmount: Float,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Float
) {
    @Ignore val localDateTime = timeStamp.epochToLocalTime(timezoneId)
    @Ignore val localSunrise = sunrise.epochToLocalTime(timezoneId)
    @Ignore val localSunset = sunset.epochToLocalTime(timezoneId)
    @Ignore val localMoonrise = moonrise.epochToLocalTime(timezoneId)
    @Ignore val localMoonset = moonset.epochToLocalTime(timezoneId)

}
