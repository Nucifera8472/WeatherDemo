package at.nuceria.weatherdemo.data.model

data class WeatherData(
    val currentWeatherData: CurrentWeatherData,
    val todayOverView: DailyWeatherData,
    val forecastWeatherData: List<DailyWeatherData>
)
