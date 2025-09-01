package com.example.earthapp.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.mapbox.geojson.Point

object GetCurrentLocation {

    private const val LOCATION_PERMISSION_REQUEST = 1001

    fun fetch(
        activity: Activity,
        locationClient: FusedLocationProviderClient,
        resolutionForResult: ActivityResultLauncher<IntentSenderRequest>,
        onLocationReceived: (Point) -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(activity)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val point = Point.fromLngLat(location.longitude, location.latitude)
                        onLocationReceived(point)
                    } else {
                        Toast.makeText(activity, "Unable to get current location", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: Exception) {
                    Log.e("GetCurrentLocation", "Error showing location settings dialog", sendEx)
                }
            } else {
                Toast.makeText(activity, "Location services unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
