package com.example.earthapp

import android.animation.ValueAnimator
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.earthapp.databinding.ActivityGlobeBinding
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.skyLayer
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.layers.properties.generated.SkyType
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.projection.generated.setProjection
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.gestures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class GlobeActivity : AppCompatActivity() {

    private val binding: ActivityGlobeBinding by lazy {
        ActivityGlobeBinding.inflate(layoutInflater)
    }

    private var globeCenter = Point.fromLngLat(0.0, 20.0)
    private var spinAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupMap()
        setupSearch()
        setupGlobeButton()

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMap() {
        binding.mapView.mapboxMap.loadStyle(
            Style.SATELLITE_STREETS
        ) {
            it.addLayer(
                skyLayer("sky") {
                    skyType(SkyType.ATMOSPHERE)
                    skyAtmosphereSun(listOf(0.0, 90.0))
                    skyAtmosphereSunIntensity(15.0)
                }
            )

            binding.mapView.mapboxMap.setProjection(projection(ProjectionName.GLOBE))

            binding.mapView.gestures.apply {
                rotateEnabled = true
                pinchToZoomEnabled = true
                doubleTapToZoomInEnabled = true
                quickZoomEnabled = true
                scrollEnabled = true
            }

            resetToGlobe()
            startSpinningGlobe()
        }
    }

    private fun setupSearch() {
        binding.searchlocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                val query = binding.searchlocation.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchLocation(query)
                } else {
                    Toast.makeText(this, "Enter a location", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupGlobeButton() {
        binding.btn3D.setOnClickListener {
            resetToGlobe()
        }
    }

    private fun searchLocation(locationName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@GlobeActivity, Locale.getDefault())
                val results = geocoder.getFromLocationName(locationName, 1)

                withContext(Dispatchers.Main) {
                    if (!results.isNullOrEmpty()) {
                        val loc = results[0]
                        val lat = loc.latitude
                        val lon = loc.longitude
                        val searchedPoint = Point.fromLngLat(lon, lat)

                        moveCameraToLocation(searchedPoint)
                    } else {
                        Toast.makeText(
                            this@GlobeActivity,
                            "Location not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("GlobeActivity", "Search error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GlobeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun moveCameraToLocation(point: Point) {
        stopSpinningGlobe()
        binding.mapView.mapboxMap.flyTo(
            CameraOptions.Builder()
                .center(point)
                .zoom(4.5)  // closer view
                .pitch(45.0)
                .build(),
            MapAnimationOptions.mapAnimationOptions { duration(3000) }
        )
    }

    private fun resetToGlobe() {
        stopSpinningGlobe()
        binding.mapView.mapboxMap.flyTo(
            CameraOptions.Builder()
                .center(globeCenter)
                .zoom(1.2)
                .pitch(0.0)
                .build(),
            MapAnimationOptions.mapAnimationOptions { duration(2000) } // smoother reset
        )
        startSpinningGlobe()
    }

    private fun startSpinningGlobe() {
        stopSpinningGlobe()
        spinAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 60000L // 60s = slower spin
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                val longitude = (animator.animatedValue as Float).toDouble()
                val spinningCenter = Point.fromLngLat(longitude, globeCenter.latitude())
                binding.mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(spinningCenter)
                        .zoom(1.2)
                        .build()
                )
            }
            start()
        }
    }

    private fun stopSpinningGlobe() {
        spinAnimator?.cancel()
        spinAnimator = null
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSpinningGlobe()
    }
}