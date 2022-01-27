package at.nuceria.weatherdemo.location

import android.content.Context
import android.location.Location
import at.nuceria.weatherdemo.util.Resource
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationHandler @Inject constructor(@ApplicationContext context: Context) {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // we are caching the last location to decide if we want to get newer data or not
    val lastLocation: LastLocation? = null

    // translate from the callback world into coroutine world with suspendCoroutine builder
    suspend fun getCurrentLocation() = suspendCoroutine<Resource<Location>> { continuation ->
        // if the cached data is no more than 5min old, we return the old data, it's unlikely
        // the user has moved a lot in between
        if (lastLocation != null && lastLocation.timestamp.plusMinutes(5).isAfterNow) {
            continuation.resume(Resource.Success(lastLocation.location))
            return@suspendCoroutine
        }
        try {

            val tokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_LOW_POWER,
                tokenSource.token
            ).addOnSuccessListener { location: Location? ->
                // In some rare situations location can be null
                if (location != null) {
                    continuation.resume(Resource.Success(location))
                } else {
                    continuation.resume(Resource.Error(LocationNullError()))
                }
            }.addOnFailureListener {
                continuation.resume(Resource.Error(it))
            }.addOnCanceledListener {
                continuation.resume(Resource.Error(LocationTimeOutError()))
            }

        } catch (e: SecurityException) {
            // Permission has been lost in the meantime
            continuation.resumeWithException(MissingLocationPermissionError())
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    data class LastLocation(
        val timestamp: DateTime,
        val location: Location
    )

    class LocationTimeOutError : Throwable()

    class LocationNullError : Throwable()

    class MissingLocationPermissionError : Throwable()
}
