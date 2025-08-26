package com.example.earthapp

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.mapbox.maps.MapView

class LocationSearch : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var locationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001
    private lateinit var locationInput: EditText

//    private val pointAnnotationManager by lazy{
//        mapView.
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.location)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}