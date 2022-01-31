package at.nuceria.weatherdemo.data.location

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHandler @Inject constructor(@ApplicationContext context: Context) {

    // we are caching the last location to decide if we want to get newer data or not
    val lastLocation: LastLocation? = null

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun getCurrentLocation(): Location {
        // if the cached data is no more than 5min old, we return the old data, it's unlikely
        // the user has moved a lot in between
        if (lastLocation != null && lastLocation.timestamp.plusMinutes(5).isAfterNow) {
            return lastLocation.location
        }
        return fusedLocationClient.awaitCurrentLocation()
    }

    data class LastLocation(
        val timestamp: DateTime,
        val location: Location
    )

    class LocationTimeOutError : Throwable()

    class LocationNullError : Throwable()

    class MissingLocationPermissionError : Throwable()
}


suspend fun FusedLocationProviderClient.awaitCurrentLocation(): Location =
    suspendCancellableCoroutine { continuation ->
        Timber.d("LocationHandler getCurrentLocation")
        val tokenSource = CancellationTokenSource()
        try {
            getCurrentLocation(
                LocationRequest.PRIORITY_LOW_POWER,
                tokenSource.token
            ).addOnSuccessListener { location: Location? ->
                // In some rare situations location can be null
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resumeWithException(LocationHandler.LocationNullError())
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }.addOnCanceledListener {
                continuation.resumeWithException(LocationHandler.LocationTimeOutError())
            }
        } catch (e: SecurityException) {
            // Permission has been lost in the meantime
            continuation.resumeWithException(LocationHandler.MissingLocationPermissionError())
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
        continuation.invokeOnCancellation {
            tokenSource.cancel()
        }
    }
