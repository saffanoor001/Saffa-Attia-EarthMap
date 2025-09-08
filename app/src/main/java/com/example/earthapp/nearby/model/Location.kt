package com.example.earthapp.nearby.model

data class Location(
    val address: String,
    val country: String,
    val formatted_address: String,
    val locality: String,
    val postcode: String,
    val region: String
)