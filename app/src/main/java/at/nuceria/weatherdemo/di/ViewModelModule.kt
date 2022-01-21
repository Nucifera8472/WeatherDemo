package at.nuceria.weatherdemo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.nuceria.weatherdemo.ui.main.MainViewModel
import com.itranslate.appkit.di.ViewModelFactory
import com.itranslate.appkit.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    // Add all ViewModel Bindings here to enable injecting them via dagger into the ViewModelFactory

    companion object {
    }

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindHomeViewModel(viewModel: MainViewModel): ViewModel

}
