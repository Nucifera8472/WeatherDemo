package at.nuceria.weatherdemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import at.nuceria.weatherdemo.data.model.CurrentWeatherData
import at.nuceria.weatherdemo.data.model.DailyWeatherData

@Database(entities = [CurrentWeatherData::class, DailyWeatherData::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun dailyWeatherDao(): DailyWeatherDao
}
