package com.example.earthapp.nearby

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.earthapp.R
import com.example.earthapp.databinding.ActivityNearbyPlacesBinding
import com.example.kotlinproject.nearby.model.Categories

class NearbyPlaces : AppCompatActivity() {

    private val binding: ActivityNearbyPlacesBinding by lazy {
        ActivityNearbyPlacesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val placeCategories = listOf(
            Categories("Theater", R.drawable.theater_1, "63be6904847c3692a84b9c25"),
            Categories("Bar", R.drawable.bar_1, "63be6904847c3692a84b9c26"),
            Categories("Temple", R.drawable.temple_1, "63be6904847c3692a84b9c27"),
            Categories("Bus Stop", R.drawable.bus_stop_1, "63be6904847c3692a84b9c28"),
            Categories("Stadium", R.drawable.stadium_1, "63be6904847c3692a84b9c29"),
            Categories("Zoo", R.drawable.zoo_1, "63be6904847c3692a84b9c2a"),
            Categories("Train Station", R.drawable.train_station_1, "63be6904847c3692a84b9c2b"),
            Categories("Shopping Center", R.drawable.shopping_center_1, "63be6904847c3692a84b9c2c"),
            Categories("Shoe Shop", R.drawable.shoe_shop_1, "63be6904847c3692a84b9c2d"),
            Categories("Pharmacy", R.drawable.pharmacy_1, "63be6904847c3692a84b9c2e"),
            Categories("Pet Shop", R.drawable.pet_shop_1, "63be6904847c3692a84b9c2f"),
            Categories("Jewellery", R.drawable.jewellery_1, "63be6904847c3692a84b9c30"),
            Categories("Makeover", R.drawable.makeover_1, "63be6904847c3692a84b9c31"),
            Categories("Gas Pump", R.drawable.gas_pump_1, "63be6904847c3692a84b9c32"),
            Categories("Doctor", R.drawable.doctor_1, "63be6904847c3692a84b9c33"),
            Categories("Dental Checkup", R.drawable.dental_checkup_1, "63be6904847c3692a84b9c34"),
            Categories("Boutique", R.drawable.boutique_1, "63be6904847c3692a84b9c35"),
            Categories("Church", R.drawable.church_1, "63be6904847c3692a84b9c36"),
            Categories("Service Station", R.drawable.service_station_1, "63be6904847c3692a84b9c37"),
            Categories("Cafe", R.drawable.cafe_1, "63be6904847c3692a84b9c38"),
            Categories("Bakery", R.drawable.bakery_1, "63be6904847c3692a84b9c39"),
            Categories("Theme Park", R.drawable.theme_park_1, "63be6904847c3692a84b9c3a"),
            Categories("Police Station", R.drawable.police_station_1, "63be6904847c3692a84b9c3b"),
            Categories("Fire Station", R.drawable.fire_station_1, "63be6904847c3692a84b9c3c"),
            Categories("School", R.drawable.school_1, "63be6904847c3692a84b9c3d"),
            Categories("Post Office", R.drawable.post_office_1, "63be6904847c3692a84b9c3e"),
            Categories("ATM", R.drawable.atm_1, "63be6904847c3692a84b9c3f"),
            Categories("Bank", R.drawable.bank_1, "63be6904847c3692a84b9c40"),
            Categories("Airport", R.drawable.airport_1, "63be6904847c3692a84b9c41"),
            Categories("Building", R.drawable.building__3__1, "63be6904847c3692a84b9c42"),
            Categories("Hotel", R.drawable.hotel_1, "63be6904847c3692a84b9c43"),
            Categories("Hospital", R.drawable.hospital_1, "63be6904847c3692a84b9c44"),
            Categories("Parking Lot", R.drawable.parking_lot_2, "63be6904847c3692a84b9c45"),
            Categories("Painter", R.drawable.painter_1, "63be6904847c3692a84b9c46"),
            Categories("Gas Station", R.drawable.gas_pump_1, "63be6904847c3692a84b9c47"),
            Categories("House Cleaning", R.drawable.house_cleaning_1, "63be6904847c3692a84b9c48"),
            Categories("Car Repair", R.drawable.car_repair_1, "63be6904847c3692a84b9c49"),
            Categories("Gallery", R.drawable.gallery__1__1, "63be6904847c3692a84b9c4a")
        )

        val categoryAdapter = CategoryAdapter(placeCategories) { selectedCategory ->
            val intent = Intent(this, NearbyPlacesResult::class.java)
            intent.putExtra("CATEGORY_ID", selectedCategory.id)
            startActivity(intent)
        }

        binding.categoryRecycler.apply {
            layoutManager = GridLayoutManager(this@NearbyPlaces, 3)
            adapter = categoryAdapter
        }

    }
}