package com.example.earthapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.earthapp.R
import com.example.earthapp.api.ImageSearchApi
import com.example.earthapp.databinding.ActivityStreetViewBinding
import com.example.earthapp.ui.adapter.StreetImageAdapter
import com.example.earthapp.utils.LocationPrecision
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class StreetViewActivity : AppCompatActivity() {

    private val binding: ActivityStreetViewBinding by lazy {
        ActivityStreetViewBinding.inflate(layoutInflater)
    }
    private lateinit var locationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private var isStyleLoaded = false
    private val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "GPS is required to get current location", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    // ViewPager2 / Indicator / Auto scroll
    private lateinit var adapter: StreetImageAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private val autoScrollDelay = 5000L // 5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        setupMap()
        setupSearchListener()

        binding.backk.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.precisionIcon.setOnClickListener {
            LocationPrecision.recenterToUserLocation(
                this,
                binding.mapView.mapboxMap,
                locationClient
            ) { point ->
                addMarker(point)
                getAddressFromLocation(point.latitude(), point.longitude())
                // FIX 1: also load images for the current location after recenter
                val q = resolvePlaceForQuery(point.latitude(), point.longitude())
                loadImagesForLocation("street view $q")
            }
        }

        adapter = StreetImageAdapter(emptyList())
        binding.viewPagerStreet.adapter = adapter
        binding.circleIndicator.setViewPager(binding.viewPagerStreet)
        // default image
        loadImagesForLocation("street view new york")
    }
    private fun setupMap() {
        binding.mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
            pointAnnotationManager = binding.mapView.annotations.createPointAnnotationManager()
            isStyleLoaded = true
            getCurrentLocation()
        }
    }
    private fun getAddressFromLocation(lat: Double, lon: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                // (You can display this if you want)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setupSearchListener() {
        binding.searchlocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                val locationName = binding.searchlocation.text.toString().trim()
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
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

        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

        val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
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
                    addMarker(userLocation)
                    getAddressFromLocation(lat, lon)


                    val q = resolvePlaceForQuery(lat, lon)
                    loadImagesForLocation("street view $q")

                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is com.google.android.gms.common.api.ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: Exception) {
                    Log.e("LocationSearch", "Error showing location settings dialog", sendEx)
                }
            } else {
                Toast.makeText(this, "Location services unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadImagesForLocation(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val images = ImageSearchApi.getImageSearch(query)
            withContext(Dispatchers.Main) {
                if (images.isNotEmpty()) {

                    adapter.updateData(images)

                    binding.viewPagerStreet.setCurrentItem(0, false)

                    binding.circleIndicator.setViewPager(binding.viewPagerStreet)
                    binding.circleIndicator.createIndicators(
                        adapter.itemCount,
                        0
                    )
                    binding.circleIndicator.animatePageSelected(0)

                    startAutoScroll(images.size)
                } else {
                    Toast.makeText(
                        this@StreetViewActivity,
                        "No images found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun searchLocation(locationName: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@StreetViewActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(locationName, 1)

                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val lat = address.latitude
                        val lon = address.longitude
                        val searchedPoint = Point.fromLngLat(lon, lat)
                        moveCameraToLocation(searchedPoint)
                        addMarker(searchedPoint)
                        getAddressFromLocation(lat, lon)

                        loadImagesForLocation("street view $locationName")

                    } else {
                        Toast.makeText(
                            this@StreetViewActivity,
                            "Location not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("LocationSearch", "Error in geocoding", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@StreetViewActivity,
                        "Search error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun moveCameraToLocation(point: Point) {
        binding.mapView.mapboxMap.flyTo(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build(),
            MapAnimationOptions.mapAnimationOptions { duration(3000) }
        )
    }
    private fun addMarker(point: Point) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker)
        val markerOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmap)

        pointAnnotationManager.let { manager ->
            manager.deleteAll()
            manager.create(markerOptions)
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

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val q = resolvePlaceForQuery(it.latitude, it.longitude)
                        loadImagesForLocation("street view $q")
                    }
                }
            }

        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    // Auto-scroll ViewPager2
    private fun startAutoScroll(itemCount: Int) {
        runnable?.let { handler.removeCallbacks(it) }
        runnable = object : Runnable {
            override fun run() {
                val nextItem = (binding.viewPagerStreet.currentItem + 1) % itemCount
                binding.viewPagerStreet.setCurrentItem(nextItem, true)
                handler.postDelayed(this, autoScrollDelay)
            }
        }
        handler.postDelayed(runnable!!, autoScrollDelay)
    }

    override fun onDestroy() {
        super.onDestroy()
        runnable?.let { handler.removeCallbacks(it) }
    }

    private fun resolvePlaceForQuery(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addr = geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()

            addr?.locality
                ?: addr?.subAdminArea
                ?: addr?.adminArea
                ?: addr?.countryName
                ?: "$lat,$lon"
        } catch (_: Exception) {
            "$lat,$lon"
        }
    }
}


