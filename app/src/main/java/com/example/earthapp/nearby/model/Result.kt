package com.example.earthapp.nearby.model

data class Result(
    val categories: List<Category>,
    val date_created: String,
    val date_refreshed: String,
    val distance: Int,
    val email: String,
    val extended_location: ExtendedLocation,
    val fsq_place_id: String,
    val latitude: Double,
    val link: String,
    val location: Location,
    val longitude: Double,
    val name: String,
    val placemaker_url: String,
    val related_places: RelatedPlaces,
    val social_media: SocialMedia,
    val tel: String
)