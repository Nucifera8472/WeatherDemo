package at.nuceria.weatherdemo.di

import at.nuceria.weatherdemo.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector
    abstract fun bindMainFragment(): MainFragment

}
