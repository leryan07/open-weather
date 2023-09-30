package com.example.openweather.network

import com.example.openweather.network.model.OpenWeatherResponse
import com.example.openweather.network.model.Weather
import com.example.openweather.network.model.WeatherMain
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeOpenWeatherApiService : OpenWeatherApiService {
    override suspend fun getWeatherByQuery(query: String): Response<OpenWeatherResponse> {
        return Response.error(404, "".toResponseBody())
    }

    override suspend fun getWeatherByCurrentLocation(
        lat: Double,
        lon: Double
    ): Response<OpenWeatherResponse> {
        val weatherMain = WeatherMain(
            0F,
            0F,
            0F,
            0F,
            0
        )
        val openWeatherResponse = OpenWeatherResponse(listOf(), weatherMain, "Indianapolis")
        return Response.success(openWeatherResponse)
    }
}