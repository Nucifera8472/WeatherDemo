package at.nuceria.weatherdemo.ui.main

import android.location.Location
import androidx.lifecycle.ViewModel
import at.nuceria.weatherdemo.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val location: Location = Location("dummy_provider").apply {
        // Location hardcoded for initial API testing
        latitude = 48.2082
        longitude = 16.3738
    }

    val weatherResult = weatherRepository.getWeather(location.latitude, location.longitude)


}
