package at.nuceria.weatherdemo.data.model

/**
 * supported weather conditions that are displayed with unique icons
 */
enum class WeatherCondition(val id: Int) {
    CLEAR(0),
    FEW_CLOUDS(1),
    SCATTERED_CLOUDS(2),
    BROKEN_CLOUDS(3),
    RAIN(4),
    RAIN_SHOWERS(5), // higher intensity rain, but in shorter bursts
    THUNDER_STORM(6),
    SNOW(7),
    MIST(8),
    FREEZING_RAIN(9);

    companion object {
        fun fromId(id: Int) = WeatherCondition.values().first { it.id == id }
    }
}
