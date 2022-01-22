package at.nuceria.weatherdemo.ui.main

import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.remote.response.WeatherCondition

/**
 * Maps OpenWeatherMap weather condition ids to local icons
 */
fun WeatherCondition.getDayIcon(): Int {
    return when(this.id) {
        // thunderstorm
        in 200..299 -> R.drawable.storm
        // drizzle
        in 300..399 -> R.drawable.rain_showers
        // rain
        in 500..510 -> R.drawable.rain_showers
        // freezing rain
        511 -> R.drawable.snow
        // showers
        in 512..599 -> R.drawable.rain
        // snow
        in 600..699 -> R.drawable.snow
        // mist/fog/haze
        in 700..799 -> R.drawable.fog
        // clear
        800 -> R.drawable.sun
        // few clouds: 11-25%
        801 -> R.drawable.cloudy_day
        // scattered clouds: 25-50%
        802 -> R.drawable.cloud
        // broken clouds: 51-84%
        803 -> R.drawable.clouds
        // overcast clouds: 85-100%
        804 -> R.drawable.clouds

        // placeholder for unknown weather conditions
        else -> R.drawable.sun
    }
}
