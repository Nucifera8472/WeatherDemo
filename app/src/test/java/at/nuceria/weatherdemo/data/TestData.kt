package at.nuceria.weatherdemo.data

import at.nuceria.weatherdemo.data.remote.response.*
import org.joda.time.DateTime

object TestData {

    private val outDatedTimeStamps: List<Long>
        get() {
            val requestTimestamp = DateTime.now().minusDays(1)
            val timestamps = mutableListOf<DateTime>()
            for (i in 0..7) {
                timestamps.add(requestTimestamp.plusDays(i))
            }
            return timestamps.map { it.millis / 1000 }
        }

    private val currentTimeStamps: List<Long>
        get() {
            val requestTimestamp = DateTime.now()
            val timestamps = mutableListOf<DateTime>()
            for (i in 0..7) {
                timestamps.add(requestTimestamp.plusDays(i))
            }
            return timestamps.map { it.millis / 1000 }
        }


    private fun getWeatherResponse(timeStamps: List<Long>): WeatherResponse =
        WeatherResponse(
            11.1, 33.3,
            CurrentWeather(
                timeStamps[0], 12.1f, 10.2f, 2.3f,
                179, listOf(Weather(800, "clear"), Weather(700, "fog"))
            ),
            listOf(
                DailyWeather(
                    timeStamps[0], DayTemperatures(8.5f, 9.5f, 7.5f, 3.5f, 2f, 10f),
                    DayFeelsLikeTemperatures(8f, 9f, 7f, 3f), 1.1f, 123,
                    listOf(
                        Weather(500, "rain"), Weather(600, "snow")
                    )
                ),
                DailyWeather(
                    timeStamps[1], DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    )
                ),
                DailyWeather(
                    timeStamps[2], DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    )
                ),
                DailyWeather(
                    timeStamps[3], DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    )
                ),
                DailyWeather(
                    timeStamps[4], DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    )
                ),
                DailyWeather(
                    timeStamps[5], DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    )
                ),
                DailyWeather(
                    timeStamps[6], DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    )
                ),
                DailyWeather(
                    timeStamps[7], DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    )
                ),
            )
        )

    val currentWeatherResponse = getWeatherResponse(currentTimeStamps)
    val outdatedWeatherResponse = getWeatherResponse(outDatedTimeStamps)


//
//    val validCurrentWeather = CurrentWeatherData(
//        id = 1,
//        timeStamp = 1234L,
//        latitude = 55.5,
//        longitude = 66.6,
//        temperature = 24.3f,
//        windSpeed = 3.23f,
//        windDegrees = 78,
//        feelsLikeTemperature = 23.1f,
//        condition = WeatherCondition.BROKEN_CLOUDS,
//        localizedDescription = "broken clouds"
//    )
//
//    val validDailyWeather = listOf(
//        DailyWeatherData(
//            timeStamp = 23L,
//            latitude = 56.6,
//            longitude = 67.6,
//            morningTemperature = 20f,
//            dayTemperature = 25f,
//            eveningTemperature = 18f,
//            nightTemperature = 12f,
//            minTemperature = 11f,
//            maxTemperature = 21f,
//            morningFeelsLikeTemperature = 19.5f,
//            dayFeelsLikeTemperature = 24.5f,
//            eveningFeelsLikeTemperature = 17.5f,
//            nightFeelsLikeTemperature = 11.5f,
//            condition = WeatherCondition.SCATTERED_CLOUDS,
//            localizedDescription = "scattered clouds",
//            windSpeed = 1.4f,
//            windDegrees = 90
//        ),
//        DailyWeatherData(
//            timeStamp = 23L,
//            latitude = 56.6,
//            longitude = 67.6,
//            morningTemperature = 20f,
//            dayTemperature = 25f,
//            eveningTemperature = 18f,
//            nightTemperature = 12f,
//            minTemperature = 11f,
//            maxTemperature = 21f,
//            morningFeelsLikeTemperature = 19.5f,
//            dayFeelsLikeTemperature = 24.5f,
//            eveningFeelsLikeTemperature = 17.5f,
//            nightFeelsLikeTemperature = 11.5f,
//            condition = WeatherCondition.SCATTERED_CLOUDS,
//            localizedDescription = "scattered clouds",
//            windSpeed = 1.4f,
//            windDegrees = 90
//        ),
//        DailyWeatherData(
//            timeStamp = 23L,
//            latitude = 56.6,
//            longitude = 67.6,
//            morningTemperature = 20f,
//            dayTemperature = 25f,
//            eveningTemperature = 18f,
//            nightTemperature = 12f,
//            minTemperature = 11f,
//            maxTemperature = 21f,
//            morningFeelsLikeTemperature = 19.5f,
//            dayFeelsLikeTemperature = 24.5f,
//            eveningFeelsLikeTemperature = 17.5f,
//            nightFeelsLikeTemperature = 11.5f,
//            condition = WeatherCondition.SCATTERED_CLOUDS,
//            localizedDescription = "scattered clouds",
//            windSpeed = 1.4f,
//            windDegrees = 90
//        )
//    )
//    val validCurrentWeatherFlow = flowOf(validCurrentWeather)
//    val validDailyWeatherFlow = flowOf(validDailyWeather)

}
