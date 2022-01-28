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
                timeStamps[0], "America/Chicago", 12.1f, 10.2f, 1000, 54, 2.3f,
                179, listOf(Weather(800, "clear"), Weather(700, "fog")), Rain(0.2f), null
            ),
            listOf(
                DailyWeather(
                    timeStamps[0], "America/Chicago", DayTemperatures(8.5f, 9.5f, 7.5f, 3.5f, 2f, 10f),
                    DayFeelsLikeTemperatures(8f, 9f, 7f, 3f), 1.1f, 123,
                    listOf(
                        Weather(500, "rain"), Weather(600, "snow")
                    ),
                    0.2f,
                    1000,
                    40,
                    8.0f,
                    timeStamps[0]+1,
                    timeStamps[0]+2,
                    timeStamps[0]+3,
                    timeStamps[0]+4,
                    0f,
                    1.1f,
                    null
                ),
                DailyWeather(
                    timeStamps[1], "America/Chicago", DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    ),
                    0.21f,
                    1001,
                    41,
                    8.1f,
                    timeStamps[0]+11,
                    timeStamps[0]+12,
                    timeStamps[0]+13,
                    timeStamps[0]+14,
                    0.1f,
                    1.11f,
                    null
                ),
                DailyWeather(
                    timeStamps[2], "America/Chicago", DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    ),
                    0.22f,
                    1002,
                    42,
                    8.2f,
                    timeStamps[0]+21,
                    timeStamps[0]+22,
                    timeStamps[0]+23,
                    timeStamps[0]+24,
                    0.2f,
                    1.12f,
                    null
                ),
                DailyWeather(
                    timeStamps[3], "America/Chicago", DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    ),
                    0.23f,
                    1003,
                    43,
                    8.3f,
                    timeStamps[0]+31,
                    timeStamps[0]+32,
                    timeStamps[0]+33,
                    timeStamps[0]+34,
                    0.3f,
                    1.13f,
                    null
                ),
                DailyWeather(
                    timeStamps[4], "America/Chicago", DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    ),
                    0.24f,
                    1004,
                    44,
                    8.4f,
                    timeStamps[0]+41,
                    timeStamps[0]+42,
                    timeStamps[0]+43,
                    timeStamps[0]+44,
                    0.4f,
                    1.14f,
                    null
                ),
                DailyWeather(
                    timeStamps[5], "America/Chicago", DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    ),
                    0.25f,
                    1005,
                    45,
                    8.5f,
                    timeStamps[0]+51,
                    timeStamps[0]+52,
                    timeStamps[0]+53,
                    timeStamps[0]+54,
                    0.5f,
                    1.15f,
                    null
                ),
                DailyWeather(
                    timeStamps[6], "America/Chicago", DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    ),
                    0.26f,
                    1006,
                    46,
                    8.6f,
                    timeStamps[0]+61,
                    timeStamps[0]+62,
                    timeStamps[0]+63,
                    timeStamps[0]+64,
                    0.6f,
                    1.16f,
                    null
                ),
                DailyWeather(
                    timeStamps[7], "America/Chicago", DayTemperatures(16.5f, 18.5f, 14.5f, 6.5f, 4f, 20f),
                    DayFeelsLikeTemperatures(16f, 18f, 14f, 6f), 12.2f, 456,
                    listOf(
                        Weather(801, "cloud"), Weather(200, "thunder")
                    ),
                    0.27f,
                    1007,
                    47,
                    8.7f,
                    timeStamps[0]+71,
                    timeStamps[0]+72,
                    timeStamps[0]+73,
                    timeStamps[0]+74,
                    0.7f,
                    1.17f,
                    null
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
