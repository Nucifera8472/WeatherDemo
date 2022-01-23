package at.nuceria.weatherdemo.data.local

import androidx.room.*
import at.nuceria.weatherdemo.data.model.CurrentWeatherData
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {
    // as long as we support only 1 location we will only keep one entry in this table
    // one shot read
    @Query("SELECT * FROM CurrentWeatherData LIMIT 1")
    suspend fun getCurrentWeather(): CurrentWeatherData?

    // observable flow, in case the entries are changed in the background by a worker for example
    @Query("SELECT * FROM CurrentWeatherData LIMIT 1")
    fun getCurrentWeatherFlow(): Flow<CurrentWeatherData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg data: CurrentWeatherData)

    @Delete
    suspend fun delete(vararg entries: CurrentWeatherData)

    @Query("DELETE FROM CurrentWeatherData")
    suspend fun deleteAll()

}

