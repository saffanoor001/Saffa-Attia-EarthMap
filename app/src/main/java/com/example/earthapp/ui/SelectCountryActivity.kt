package com.example.earthapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.earthapp.MainActivity
import com.example.earthapp.R
import com.example.earthapp.api.ApiClient
import com.example.earthapp.model.Country
import com.example.earthapp.ui.adapter.CountryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectCountryActivity : AppCompatActivity() {

    private lateinit var rvCountries: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var adapter: CountryAdapter
    private val countries = mutableListOf<Country>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_country)

        tvTitle = findViewById(R.id.tvSelectCountry)
        rvCountries = findViewById(R.id.rvCountries)
        val btnBack = findViewById<ImageButton>(R.id.backButton) // ← add this button in XML

        rvCountries.layoutManager = GridLayoutManager(this, 3)
        adapter = CountryAdapter(countries) { country ->
            val i = Intent(this, WebcamsActivity::class.java)
            i.putExtra("COUNTRY_CODE", country.code)
            startActivity(i)
        }
        rvCountries.adapter = adapter

        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        fetchCountries()
    }

    private fun fetchCountries() {
        ApiClient.windyService.getCountries().enqueue(object : Callback<List<Country>> {
            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        countries.clear()
                        countries.addAll(body)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@SelectCountryActivity, "No countries", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SelectCountryActivity, "Failed to fetch countries", Toast.LENGTH_SHORT).show()
                    Log.e("SelectCountry", "Response not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Toast.makeText(this@SelectCountryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("SelectCountry", "onFailure", t)
            }
        })
    }
}