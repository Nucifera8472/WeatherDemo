package at.nuceria.weatherdemo.data.local

import androidx.room.*
import at.nuceria.weatherdemo.data.model.DailyWeatherData
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWeatherDao {
    @Query("SELECT * FROM DailyWeatherData WHERE timeStamp = :date")
    fun getDailyWeather(date: Long): DailyWeatherData?

    // one shot read
    @Query("SELECT * FROM DailyWeatherData WHERE timeStamp >= :startDate")
    fun getForecastData(startDate: Long): List<DailyWeatherData>

    // observable flow, in case the entries are changed in the background by a worker for example
    @Query("SELECT * FROM DailyWeatherData ORDER BY timeStamp ASC")
    fun getForecastDataFlow(): Flow<List<DailyWeatherData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<DailyWeatherData>)

    @Delete
    suspend fun delete(vararg entries: DailyWeatherData)

    @Query("DELETE FROM DailyWeatherData")
    suspend fun deleteAll()
}
