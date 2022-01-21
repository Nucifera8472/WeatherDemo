package at.nuceria.weatherdemo.di

import at.nuceria.weatherdemo.MainApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(target: MainApplication)
}
