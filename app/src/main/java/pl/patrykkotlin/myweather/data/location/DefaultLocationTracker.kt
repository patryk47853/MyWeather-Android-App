package pl.patrykkotlin.myweather.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.patrykkotlin.myweather.domain.location.LocationTracker
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        val locationPermissionGranted = checkLocationPermission()
        val isGpsEnabled = checkGpsEnabled()

        if (!locationPermissionGranted || !isGpsEnabled) {
            return null
        }

        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)

        return suspendCancellableCoroutine { cont ->
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    cont.resume(location)
                    locationClient.removeLocationUpdates(this)
                }
            }

            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )

            cont.invokeOnCancellation {
                locationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        return (ContextCompat.checkSelfPermission(application, fineLocationPermission)
                == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(application, coarseLocationPermission)
                        == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkGpsEnabled(): Boolean {
        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
