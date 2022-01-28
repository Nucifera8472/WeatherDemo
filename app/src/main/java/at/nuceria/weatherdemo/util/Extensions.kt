package at.nuceria.weatherdemo.util

import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.model.CurrentWeatherData
import at.nuceria.weatherdemo.data.model.DailyWeatherData
import at.nuceria.weatherdemo.data.model.WeatherCondition
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.data.remote.response.Weather
import at.nuceria.weatherdemo.data.remote.response.WeatherResponse
import timber.log.Timber

/**
 * Maps weather condition ids to local icons
 */
fun WeatherCondition.getDayIcon(): Int {
    return when (this) {
        WeatherCondition.CLEAR -> R.drawable.sun
        WeatherCondition.FEW_CLOUDS -> R.drawable.cloudy_day
        WeatherCondition.SCATTERED_CLOUDS -> R.drawable.cloud
        WeatherCondition.BROKEN_CLOUDS -> R.drawable.clouds
        WeatherCondition.RAIN -> R.drawable.rain
        WeatherCondition.RAIN_SHOWERS -> R.drawable.rain_showers
        WeatherCondition.THUNDER_STORM -> R.drawable.storm
        WeatherCondition.SNOW -> R.drawable.snow
        WeatherCondition.MIST -> R.drawable.fog
        WeatherCondition.FREEZING_RAIN -> R.drawable.snow  // TODO switch icon
    }
}

fun WeatherResponse.toWeatherData(): WeatherData {
    val dailyWeatherData = dailyWeather.map {
        DailyWeatherData(
            timeStamp = it.dateTime,
            latitude = lat,
            longitude = lon,
            morningTemperature = it.dayTemperatures.morn,
            dayTemperature = it.dayTemperatures.day,
            eveningTemperature = it.dayTemperatures.eve,
            nightTemperature = it.dayTemperatures.night,
            minTemperature = it.dayTemperatures.min,
            maxTemperature = it.dayTemperatures.max,
            morningFeelsLikeTemperature = it.feelsLikeTemperatures.morn,
            dayFeelsLikeTemperature = it.feelsLikeTemperatures.day,
            eveningFeelsLikeTemperature = it.feelsLikeTemperatures.eve,
            nightFeelsLikeTemperature = it.feelsLikeTemperatures.night,
            condition = it.weatherConditions.first().toWeatherCondition(),
            localizedDescription = it.weatherConditions.first().description,
            windSpeed = it.windSpeed,
            windDegrees = it.windDegrees
        )
    }

    val weather = currentWeather.weathers.first()

    val currentWeatherData = CurrentWeatherData(
        id = 0,
        timeStamp = currentWeather.requestTimestamp,
        latitude = lat,
        longitude = lon,
        temperature = currentWeather.temp,
        feelsLikeTemperature = currentWeather.feelsLikeTemp,
        windSpeed = currentWeather.windSpeed,
        windDegrees = currentWeather.windDegrees,
        condition = weather.toWeatherCondition(),
        localizedDescription = weather.description
    )

    val todayOverview = dailyWeatherData.first()
    val forecasts = dailyWeatherData.toMutableList().apply { removeFirst() }
    return WeatherData(currentWeatherData, todayOverview, forecasts)
}

fun Weather.toWeatherCondition(): WeatherCondition {
    return when (this.id) {
        // thunderstorm
        in 200..299 -> WeatherCondition.THUNDER_STORM
        // drizzle
        in 300..399 -> WeatherCondition.RAIN_SHOWERS
        // rain
        in 500..510 -> WeatherCondition.RAIN_SHOWERS
        // freezing rain
        511 -> WeatherCondition.FREEZING_RAIN
        // showers
        in 512..599 -> WeatherCondition.RAIN
        // snow
        in 600..699 -> WeatherCondition.SNOW
        // mist/fog/haze
        in 700..799 -> WeatherCondition.MIST
        // clear
        800 -> WeatherCondition.CLEAR
        // few clouds: 11-25%
        801 -> WeatherCondition.FEW_CLOUDS
        // scattered clouds: 25-50%
        802 -> WeatherCondition.SCATTERED_CLOUDS
        // broken clouds: 51-84%
        803 -> WeatherCondition.BROKEN_CLOUDS
        // overcast clouds: 85-100%
        804 -> WeatherCondition.BROKEN_CLOUDS

        // placeholder for unknown weather conditions (shouldn't happen with a versioned API
        else -> {
            // Send to Crashlytics to find out something went wrong
            Timber.e("Unknown weather id ${this.id}")
            WeatherCondition.FEW_CLOUDS
        }
    }
}


/**
 * @return null if the zone ID is invalid
 */
fun Long.epochToLocalTime(zoneId: String): DateTime? = try {
    epochToDateTime().withZone(DateTimeZone.forID(zoneId))
} catch (e: Exception) {
    null
}

fun Long.epochToDateTime(): DateTime = DateTime(epochToMillis(), DateTimeZone.UTC)

fun Long.epochToMillis(): Long = this * 1000
