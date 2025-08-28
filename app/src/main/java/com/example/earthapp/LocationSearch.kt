package com.example.earthapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationSearch : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var locationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001
    private lateinit var locationInput: EditText
    private var currentStyleUri = Style.MAPBOX_STREETS
    private var pointAnnotationManager: PointAnnotationManager? = null
    private var isStyleLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)
        initializeViews()
        setupMap()
        setupSearchListener()
    }

    private fun initializeViews() {
        mapView = findViewById(R.id.mapView)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationInput = findViewById(R.id.searchlocation)
    }

    private fun setupMap() {
        mapView.getMapboxMap().loadStyleUri(currentStyleUri) { style ->
            pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
            isStyleLoaded = true
            Log.d("LocationSearch", "Map style loaded and annotation manager created")
            getCurrentLocation()
        }
    }

    private fun setupSearchListener() {
        locationInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                val locationName = locationInput.text.toString().trim()
                if (locationName.isNotEmpty()) {
                    searchLocation(locationName)
                } else {
                    Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun getCurrentLocation() {
        if (!isStyleLoaded) {
            Log.w("LocationSearch", "Style not loaded yet, skipping location request")
            return
        }

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

        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

        val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient =
            com.google.android.gms.location.LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    val userLocation = Point.fromLngLat(lon, lat)
                    moveCameraToLocation(userLocation)
                    addMarker(userLocation, "Current Location")
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error getting location: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, 1002)
                } catch (sendEx: Exception) {
                    Log.e("LocationSearch", "Error showing location settings dialog", sendEx)
                }
            } else {
                Toast.makeText(this, "Location services unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLocation(locationName: String) {
        if (!isStyleLoaded) {
            Toast.makeText(this, "Map is still loading, please wait", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val geocoder = Geocoder(this@LocationSearch, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(locationName, 1)

            withContext(Dispatchers.Main) {
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val lat = address.latitude
                    val lon = address.longitude
                    val searchedPoint = Point.fromLngLat(lon, lat)
                    moveCameraToLocation(searchedPoint)
                    addMarker(searchedPoint, locationName)
                } else {
                    Toast.makeText(
                        this@LocationSearch,
                        "Location not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun moveCameraToLocation(point: Point) {
        mapView.getMapboxMap().flyTo(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build(),
            MapAnimationOptions.mapAnimationOptions { duration(3000) }
        )
    }

    private fun addMarker(point: Point, title: String) {
        if (pointAnnotationManager == null) {
            Log.e("LocationSearch", "PointAnnotationManager is null")
            pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
        }

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker)
        if (bitmap == null) {
            Log.e("LocationSearch", "Failed to decode marker bitmap")
            Toast.makeText(this, "Marker image not found", Toast.LENGTH_SHORT).show()
            return
        }

        val markerOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmap)

        pointAnnotationManager?.let { manager ->
            manager.deleteAll()
            manager.create(markerOptions)
            Log.d("LocationSearch", "Marker added at: $point")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pointAnnotationManager?.deleteAll()
    }
}