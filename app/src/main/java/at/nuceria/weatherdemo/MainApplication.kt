package at.nuceria.weatherdemo

import android.app.Application
import androidx.viewbinding.BuildConfig
import at.nuceria.weatherdemo.di.AppModule
import at.nuceria.weatherdemo.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class MainApplication : Application(), HasAndroidInjector {

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        // Build dagger injection graph
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
            .inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(
                Timber.DebugTree()
            )
        }
    }
}
