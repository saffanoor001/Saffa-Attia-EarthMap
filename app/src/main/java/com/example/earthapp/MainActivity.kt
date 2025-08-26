package com.example.earthapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.earthapp.ui.SelectCountryActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val webcamLayout = findViewById<LinearLayout>(R.id.webcam)
        webcamLayout.setOnClickListener {
            startActivity(Intent(this, SelectCountryActivity::class.java))
        }

    }
}