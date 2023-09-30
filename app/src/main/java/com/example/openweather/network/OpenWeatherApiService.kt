package com.example.openweather.network

import com.example.openweather.network.model.OpenWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "081af61ddee567b278089917ecec39de"

interface OpenWeatherApiService {
    @GET("data/2.5/weather?appid=$API_KEY")
    suspend fun getWeatherByQuery(@Query("q") query: String): Response<OpenWeatherResponse>

    @GET("data/2.5/weather?appid=$API_KEY")
    suspend fun getWeatherByCurrentLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<OpenWeatherResponse>
}