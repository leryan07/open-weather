package com.example.openweather.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenWeatherResponse(
    val weather: List<Weather>,
    val main: WeatherMain,
    val name: String
)
