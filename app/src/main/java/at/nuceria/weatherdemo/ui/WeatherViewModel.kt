package at.nuceria.weatherdemo.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.nuceria.weatherdemo.data.WeatherRepository
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.location.LocationHandler
import at.nuceria.weatherdemo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationHandler: LocationHandler
) :
    ViewModel() {

    // prevent state updates from other classes
    private val _weatherData = MutableStateFlow<Resource<out WeatherData?>>(Resource.Loading(null))

    // the state flow data is can be collected by all fragments that share this view model without
    // launching the whole flow again because it is retained in the fragment scope. Data is also
    // retained across configuration changes
    val weatherData: StateFlow<Resource<out WeatherData?>> = _weatherData


    fun retrieveWeatherForCurrentLocation() = viewModelScope.launch(Dispatchers.IO) {

        when (val currentLocation = locationHandler.getCurrentLocation()) {
            is Resource.Error -> _weatherData.value =
                Resource.Error(currentLocation.error ?: Exception())
            is Resource.Loading -> {}
            is Resource.Success -> {
                currentLocation.data?.let {
                    retrieveWeatherForLocation(it)
                } ?: kotlin.run { _weatherData.value = Resource.Error(Exception()) }
            }
        }


    }

    fun retrieveWeatherForLocation(location: Location) = viewModelScope.launch(Dispatchers.IO) {
        weatherRepository.getWeather(location)
            .collect { output -> _weatherData.value = output }
    }
}
