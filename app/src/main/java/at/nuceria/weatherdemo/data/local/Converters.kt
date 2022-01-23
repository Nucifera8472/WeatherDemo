package at.nuceria.weatherdemo.data.local

import androidx.room.TypeConverter
import at.nuceria.weatherdemo.data.model.WeatherCondition

class Converters {

    @TypeConverter
    fun toWeatherCondition(id: Int) = WeatherCondition.fromId(id)

    @TypeConverter
    fun fromWeatherCondition(condition: WeatherCondition) = condition.id

}
