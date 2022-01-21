package at.nuceria.weatherdemo.di

import android.app.Application
import android.content.Context
import at.nuceria.weatherdemo.MainApplication
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module(
    includes = [
        AndroidInjectionModule::class,
        FragmentsModule::class,
        ViewModelModule::class,
    ]
)
class AppModule(val app: MainApplication) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideApplication(): Application = app
}
