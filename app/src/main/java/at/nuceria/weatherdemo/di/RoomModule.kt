package at.nuceria.weatherdemo.di

import android.content.Context
import androidx.room.Room
import at.nuceria.weatherdemo.data.local.AppDatabase
import at.nuceria.weatherdemo.data.local.CurrentWeatherDao
import at.nuceria.weatherdemo.data.local.DailyWeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    internal fun providesRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesCurrentWeatherDao(appDatabase: AppDatabase): CurrentWeatherDao {
        return appDatabase.currentWeatherDao()
    }

    @Provides
    fun providesDailyWeatherDao(appDatabase: AppDatabase): DailyWeatherDao {
        return appDatabase.dailyWeatherDao()
    }
}
