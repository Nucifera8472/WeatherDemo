package at.nuceria.weatherdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @param timeStamp Timestamp from the API request
 */
@Entity
data class CurrentWeatherData(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val timeStamp: Long,
    val latitude: Double,
    val longitude: Double,
    val temperature: Float,
    val feelsLikeTemperature: Float,
    val windSpeed: Float,
    val windDegrees: Int,
    val condition: WeatherCondition,
    val localizedDescription: String

//    val rainAmount: Float,
//    val pressure: Int,
//    val humidity: Int,
//    val dewPoint: Float,
//    val uvIndex: Float,
    // useful so that we can show the timestamp in weather location time once we support other cities,
    // or when the user travels between timezones
//    val timeZoneOffset: Int,
)

