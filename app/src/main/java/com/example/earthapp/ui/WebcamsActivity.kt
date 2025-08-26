package com.example.earthapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.earthapp.R
import com.example.earthapp.api.ApiClient
import com.example.earthapp.model.Webcam
import com.example.earthapp.model.WebcamsResponse
import com.example.earthapp.ui.adapter.WebcamAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebcamsActivity : AppCompatActivity() {

    private lateinit var rvWebcams: RecyclerView
    private lateinit var adapter: WebcamAdapter
    private val webcams = mutableListOf<Webcam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webcams)

        rvWebcams = findViewById(R.id.rvWebcams)
        val btnBack = findViewById<ImageButton>(R.id.backButton)

        rvWebcams.layoutManager = LinearLayoutManager(this)
        adapter = WebcamAdapter(webcams)
        rvWebcams.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }



        val countryCode = intent.getStringExtra("COUNTRY_CODE") ?: ""
        if (countryCode.isBlank()) {
            Toast.makeText(this, "No country provided", Toast.LENGTH_SHORT).show()
            return
        }

        fetchWebcams(countryCode)
    }

    private fun fetchWebcams(countryCode: String) {
        ApiClient.windyService.getWebcamsByCountry(countryCode).enqueue(object : Callback<WebcamsResponse> {
            override fun onResponse(call: Call<WebcamsResponse>, response: Response<WebcamsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.webcams ?: emptyList()
                    webcams.clear()
                    webcams.addAll(list)
                    adapter.updateData(list)
                } else {
                    Toast.makeText(this@WebcamsActivity, "Failed to load webcams", Toast.LENGTH_SHORT).show()
                    Log.e("WebcamsActivity", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WebcamsResponse>, t: Throwable) {
                Toast.makeText(this@WebcamsActivity, "API error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("WebcamsActivity", "onFailure", t)
            }
        })
    }
}