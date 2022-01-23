package at.nuceria.weatherdemo.ui.main

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import at.nuceria.weatherdemo.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val location: Location = Location("dummy_provider").apply {
        // Location hardcoded for initial API testing
        latitude = 48.2082
        longitude = 16.3738
    }

    // turning into live data for convenience. It takes care of collecting the flow, no advanced
    // flow options needed at this point
    val weatherResult = weatherRepository.getWeather(location.latitude, location.longitude)
        .asLiveData(Dispatchers.IO)

}
