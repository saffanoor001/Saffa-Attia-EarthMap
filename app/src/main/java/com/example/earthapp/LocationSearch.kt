package com.example.earthapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.util.Locale

class LocationSearch : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var locationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001
    private lateinit var locationInput: EditText
    private var currentStyleUri = Style.MAPBOX_STREETS

    private val pointAnnotationManager by lazy {
        mapView.annotations.createPointAnnotationManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location_search)

        mapView = findViewById(R.id.mapView)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationInput = findViewById(R.id.searchlocation)

        mapView.getMapboxMap().loadStyleUri(currentStyleUri) {
            getCurrentLocation()
        }

        locationInput.setOnClickListener {
            val locationName = locationInput.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocation(locationName)
            } else {
                Toast.makeText(this, "Enter a Location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(locationName, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            moveCameraAndAddMarker(address.latitude, address.longitude)
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                moveCameraAndAddMarker(location.latitude, location.longitude)
            }
        }
    }

    private fun moveCameraAndAddMarker(lat: Double, lon: Double) {
        val point = Point.fromLngLat(lon, lat)

        mapView.getMapboxMap().flyTo(
            CameraOptions.Builder()
                .center(point)
                .zoom(14.0)
                .build(),
            mapAnimationOptions { duration(3000L) }
        )

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.your_location__6__1)
        pointAnnotationManager.deleteAll()
        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmap)

        pointAnnotationManager.create(pointAnnotationOptions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        }
    }
}