package com.example.earthapp.nearby

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earthapp.databinding.ActivityNearbyPlacesResultBinding
import com.example.earthapp.nearby.api.PlacesApiInstance
import com.example.earthapp.utils.GetCurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NearbyPlacesResult : AppCompatActivity() {

    private val binding: ActivityNearbyPlacesResultBinding by lazy {
        ActivityNearbyPlacesResultBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: PlacesAdapter
    private lateinit var locationClient: FusedLocationProviderClient

    private val resolutionLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchLocationAndPlaces()
            } else {
                Toast.makeText(this, "Location not enabled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvPlaces.layoutManager = LinearLayoutManager(this)

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocationAndPlaces()
    }

    private fun fetchLocationAndPlaces() {
        GetCurrentLocation.fetch(
            activity = this,
            locationClient = locationClient,
            resolutionForResult = resolutionLauncher
        ) { point: Point ->
            val latLong = "${point.latitude()},${point.longitude()}"
            val categoryId = intent.getStringExtra("CATEGORY_ID") ?: "13034"
            fetchNearbyPlaces(latLong, categoryId)
        }
    }

    private fun fetchNearbyPlaces(latLong: String, categoryId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = PlacesApiInstance.api.searchPlaces(
                    latLong = latLong,
                    radius = 1000,
                    categories = categoryId,
                    limit = 20
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val places = response.body()?.results ?: emptyList()
                        if (places.isNotEmpty()) {
                            adapter = PlacesAdapter(places)
                            binding.rvPlaces.adapter = adapter
                        } else {
                            Toast.makeText(
                                this@NearbyPlacesResult,
                                "No places found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@NearbyPlacesResult,
                            "API Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@NearbyPlacesResult,
                        "Request failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("NearbyPlacesResult", "Error fetching places", e)
                }
            }
        }
    }
}
