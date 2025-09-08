package com.example.earthapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earthapp.databinding.ActivityMainBinding
import com.example.earthapp.nearby.NearbyPlaces
import com.example.earthapp.ui.SelectCountryActivity
import com.example.earthapp.ui.StreetViewActivity

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        binding.webcam.setOnClickListener {
            startActivity(Intent(this, SelectCountryActivity::class.java))
        }

        binding.searchlocation.setOnClickListener {
            val intent = Intent(this, LocationSearch::class.java)
            startActivity(intent)
        }

        binding.streetView.setOnClickListener {
            startActivity(Intent(this, StreetViewActivity::class.java))
        }

        binding.nearbyplaces.setOnClickListener {
            startActivity(Intent(this, NearbyPlaces::class.java))
        }

    }
}