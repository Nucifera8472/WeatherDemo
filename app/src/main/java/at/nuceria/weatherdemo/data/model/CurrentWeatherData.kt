package at.nuceria.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import at.nuceria.weatherdemo.util.epochToLocalTime

/**
 *
 * @param timeStamp Timestamp from the API request
 */
@Entity
data class CurrentWeatherData(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val timeStamp: Long,
    val timezoneId: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Float,
    val feelsLikeTemperature: Float,
    val windSpeed: Float,
    val windDegrees: Int,
    val condition: WeatherCondition,
    val localizedDescription: String,
    val precipitationAmount: Float,
    val pressure: Int,
    val humidity: Int,
//    val uvIndex: Float,
) {
    @Ignore
    val localDateTime = timeStamp.epochToLocalTime(timezoneId)
}

