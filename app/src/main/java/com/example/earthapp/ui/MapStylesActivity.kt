package com.example.earthapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earthapp.R
import com.example.earthapp.databinding.ActivityMapStylesBinding
import com.example.earthapp.model.MapStyle
import com.example.earthapp.ui.adapter.MapStylesAdapter
import com.mapbox.maps.Style
import androidx.recyclerview.widget.GridLayoutManager


class MapStylesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapStylesBinding
    private lateinit var adapter: MapStylesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapStylesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStyles.layoutManager = GridLayoutManager(this, 2)
        adapter = MapStylesAdapter(getMapStyles()) { selectedStyle ->

            val resultIntent = Intent()
            resultIntent.putExtra("SELECTED_STYLE_URI", selectedStyle.styleUrl)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }
        binding.rvStyles.adapter = adapter

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getMapStyles(): List<MapStyle> {
        val token = getString(R.string.mapbox_access_token)
        return listOf(
            MapStyle(
                "Streets",
                Style.MAPBOX_STREETS,
                "https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/0,0,1/512x512?access_token=$token"
            ),
            MapStyle(
                "Outdoors",
                Style.OUTDOORS,
                "https://api.mapbox.com/styles/v1/mapbox/outdoors-v12/static/0,0,1/512x512?access_token=$token"
            ),
            MapStyle(
                "Light",
                Style.LIGHT,
                "https://api.mapbox.com/styles/v1/mapbox/light-v11/static/0,0,1/512x512?access_token=$token"
            ),
            MapStyle(
                "Dark",
                Style.DARK,
                "https://api.mapbox.com/styles/v1/mapbox/dark-v11/static/0,0,1/512x512?access_token=$token"
            ),
            MapStyle(
                "Satellite",
                Style.SATELLITE,
                "https://api.mapbox.com/styles/v1/mapbox/satellite-v9/static/0,0,1/512x512?access_token=$token"
            ),
            MapStyle(
                "Satellite Streets",
                Style.SATELLITE_STREETS,
                "https://api.mapbox.com/styles/v1/mapbox/satellite-streets-v12/static/0,0,1/512x512?access_token=$token"
            )
        )
    }
}




