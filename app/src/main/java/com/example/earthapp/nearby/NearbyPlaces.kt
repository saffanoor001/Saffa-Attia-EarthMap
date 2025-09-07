package com.example.earthapp.nearby

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.earthapp.R
import com.example.earthapp.databinding.ActivityNearbyPlacesBinding
import com.example.kotlinproject.nearby.model.Categories
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location

class NearbyPlaces : AppCompatActivity() {

    private val binding: ActivityNearbyPlacesBinding by lazy {
        ActivityNearbyPlacesBinding.inflate(layoutInflater)
    }

    private lateinit var locationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        setupMap()

        binding.backk.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val placeCategories = listOf(
            Categories("Hospital", R.drawable.hospital_1, "13044"),
            Categories("ATM", R.drawable.atm_1, "13043"),
            Categories("Theater", R.drawable.theater_1, "13037"),
            Categories("Hotel", R.drawable.hotel_1, "13029"),
            Categories("Bakery", R.drawable.bakery_1, "13033"),
            Categories("Pharmacy", R.drawable.pharmacy_1, "13036"),
            Categories("Bar", R.drawable.bar_1, "13003"),
            Categories("Temple", R.drawable.temple_1, "13027"),
            Categories("Bus Stop", R.drawable.bus_stop_1, "13002"),
            Categories("Stadium", R.drawable.stadium_1, "13010"),
            Categories("Zoo", R.drawable.zoo_1, "13068"),
            Categories("Train Station", R.drawable.train_station_1, "13006"),
            Categories("Shopping Center", R.drawable.shopping_center_1, "13004"),
            Categories("Shoe Store", R.drawable.shoe_shop_1, "13007"),
            Categories("Pet Store", R.drawable.pet_shop_1, "13009"),
            Categories("Beauty Salon", R.drawable.makeover_1, "13011"),
            Categories("Doctor", R.drawable.doctor_1, "13012"),
            Categories("Dentist", R.drawable.dental_checkup_1, "13013"),
            Categories("Clothing Store", R.drawable.boutique_1, "13014"),
            Categories("Church", R.drawable.church_1, "13015"),
            Categories("Car Repair", R.drawable.service_station_1, "13016"),
            Categories("Cafe", R.drawable.cafe_1, "13032"),
            Categories("Theme Park", R.drawable.theme_park_1, "10058"),
            Categories("Police Station", R.drawable.police_station_1, "13020"),
            Categories("Fire Station", R.drawable.fire_station_1, "13021"),
            Categories("School", R.drawable.school_1, "13022"),
            Categories("Post Office", R.drawable.post_office_1, "13023"),
            Categories("Bank", R.drawable.bank_1, "13024"),
            Categories("Airport", R.drawable.airport_1, "13026"),
            Categories("Mosque", R.drawable.building__3__1, "13028"),
            Categories("Parking", R.drawable.parking_lot_2, "19061"),
            Categories("Gas Station", R.drawable.gas_pump_1, "13033"),
            Categories("Art Gallery", R.drawable.gallery__1__1, "13036")
        )
        val categoryAdapter = CategoryAdapter(placeCategories) { selectedCategory ->
            Log.d("NearbyPlaces", "Selected category ID: ${selectedCategory.id}")
            val intent = Intent(this, NearbyPlacesResult::class.java).apply {
                putExtra("selectedCategoryId", selectedCategory.id)
                putExtra("selectedCategoryName", selectedCategory.categoryName)
            }
            startActivity(intent)
        }

        binding.categoryRecycler.apply {
            layoutManager = GridLayoutManager(this@NearbyPlaces, 3)
            adapter = categoryAdapter
        }
    }

    private fun setupMap() {
        binding.mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
            val locationComponent = binding.mapView.location
            locationComponent.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
            //TODO: remove these later and add to the util
            locationComponent.addOnIndicatorPositionChangedListener { point: Point ->
                binding.mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(point)
                        .zoom(17.0)
                        .bearing(0.0)
                        .pitch(0.0)
                        .build()
                )
                binding.mapView.gestures.focalPoint =
                    binding.mapView.mapboxMap.pixelForCoordinate(point)
            }

            locationComponent.addOnIndicatorBearingChangedListener { bearing: Double ->
                binding.mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .bearing(bearing)
                        .build()
                )
            }

            isStyleLoaded = true
            getCurrentLocation()
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

    private fun moveCameraToLocation(point: Point) {
        binding.mapView.mapboxMap.flyTo(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build(),
            MapAnimationOptions.mapAnimationOptions { duration(3000) }
        )
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
}