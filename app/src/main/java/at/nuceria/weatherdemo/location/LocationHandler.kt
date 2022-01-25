package at.nuceria.weatherdemo.location

import android.location.Location
import javax.inject.Inject

class LocationHandler @Inject constructor() {
    // Location hardcoded for initial API testing
    val lastLocation: Location = Location("dummy_provider").apply {
        latitude = 48.2082
        longitude = 16.3738
    }
}
