package com.example.earthapp.utils

import android.content.Context
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo

object LocationPrecision {

    fun recenterToUserLocation(
        context: Context,
        mapboxMap: MapboxMap,
        locationClient: FusedLocationProviderClient,
        onLocationFound: (Point) -> Unit
    ) {
        try {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->

                val point = Point.fromLngLat(location.longitude, location.latitude)
                mapboxMap.flyTo(
                    CameraOptions.Builder()
                        .center(point)
                        .zoom(16.0)
                        .bearing(0.0)
                        .pitch(0.0)
                        .build(),
                    MapAnimationOptions.mapAnimationOptions { duration(1500) }
                )
                onLocationFound(point)
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
}