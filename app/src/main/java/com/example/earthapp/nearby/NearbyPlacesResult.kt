package com.example.earthapp.nearby

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earthapp.databinding.ActivityNearbyPlacesResultBinding
import com.example.earthapp.nearby.api.PlacesApiInstance
import com.example.earthapp.utils.GetCurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NearbyPlacesResult : AppCompatActivity() {

    private val binding: ActivityNearbyPlacesResultBinding by lazy {
        ActivityNearbyPlacesResultBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: PlacesAdapter
    private lateinit var locationClient: FusedLocationProviderClient
    private var isStyleLoaded = false

    private val resolutionLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchLocationAndPlaces()
            } else {
                Toast.makeText(this, "Location not enabled", Toast.LENGTH_SHORT).show()
            }
        }

    private var categoryId: String? = null
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        categoryId = intent.getStringExtra("selectedCategoryId")
        if (categoryId.isNullOrEmpty()) {
            Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        categoryName = intent.getStringExtra("selectedCategoryName")

        binding.categoryName.text = categoryName
        binding.rvPlaces.layoutManager = LinearLayoutManager(this)
        binding.backk.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setupMap()
    }

    private fun setupMap() {
        binding.mapView.mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
            val locationComponent = binding.mapView.location
            locationComponent.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
            isStyleLoaded = true
            fetchLocationAndPlaces()
        }
    }

    private fun fetchLocationAndPlaces() {
        GetCurrentLocation.fetch(
            activity = this,
            locationClient = locationClient,
            resolutionForResult = resolutionLauncher
        ) { point: Point ->
            val latLong = "${point.latitude()},${point.longitude()}"
            moveCameraToLocation(point)
            fetchNearbyPlaces(latLong, categoryName!!)
        }
    }

    private fun fetchNearbyPlaces(latLong: String,categoryName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = PlacesApiInstance.api.searchPlaces(
                    latLong = latLong,
                    radius = 5000,
                    query = categoryName,
                    limit = 10
                )
                withContext(
                    Dispatchers.Main
                ) {
                    if (response.isSuccessful) {
                        val placesResponse = response.body()
                        val places = placesResponse?.results ?: emptyList()

                        if (places.isNotEmpty()) {
                            adapter = PlacesAdapter(places)
                            binding.rvPlaces.adapter = adapter
                            //TODO: Add markers for these places on the map like in google maps
                        } else {
                            Toast.makeText(
                                this@NearbyPlacesResult,
                                "No places found in this category",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@NearbyPlacesResult,
                        "Request failed: ${e.message}",
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
}